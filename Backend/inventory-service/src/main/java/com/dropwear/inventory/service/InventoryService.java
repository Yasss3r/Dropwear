package com.dropwear.inventory.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.dropwear.inventory.domain.*;
import com.dropwear.inventory.events.StockFaibleEvent;
import com.dropwear.inventory.events.StockReapprovisionneEvent;
import com.dropwear.inventory.events.Topics;
import com.dropwear.inventory.messaging.EventPublisher;
import com.dropwear.inventory.repository.*;
import com.dropwear.inventory.web.BusinessConflictException;
import com.dropwear.inventory.web.ReservationUnavailableException;
import com.dropwear.inventory.web.dto.InventoryDtos.*;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final StockItemRepository stockItemRepository;
    private final StockReservationRepository reservationRepository;
    private final StockMovementRepository movementRepository;
    private final RestockAlertRepository alertRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final EventPublisher eventPublisher;
    private final int defaultReorderThreshold;

    public InventoryService(
            StockItemRepository stockItemRepository,
            StockReservationRepository reservationRepository,
            StockMovementRepository movementRepository,
            RestockAlertRepository alertRepository,
            ProcessedEventRepository processedEventRepository,
            EventPublisher eventPublisher,
            @Value("${dropwear.stock.default-reorder-threshold:5}") int defaultReorderThreshold) {
        this.stockItemRepository = stockItemRepository;
        this.reservationRepository = reservationRepository;
        this.movementRepository = movementRepository;
        this.alertRepository = alertRepository;
        this.processedEventRepository = processedEventRepository;
        this.eventPublisher = eventPublisher;
        this.defaultReorderThreshold = defaultReorderThreshold;
    }

    // ==========================================
    // STOCKS REST API
    // ==========================================

    @Transactional(readOnly = true)
    public List<StockDto> getStocks(List<String> skus) {
        if (skus == null || skus.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, StockItem> itemsBySku = stockItemRepository.findBySkuIn(skus).stream()
                .collect(Collectors.toMap(StockItem::getSku, item -> item));

        return skus.stream().map(sku -> {
            StockItem item = itemsBySku.get(sku);
            int available = (item != null) ? item.available() : 0;
            return new StockDto(sku, available, available > 0);
        }).toList();
    }

    @Transactional(readOnly = true)
    public StockDto getStock(String sku) {
        StockItem item = stockItemRepository.findBySku(sku)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SKU non trouvé : " + sku));
        int available = item.available();
        return new StockDto(sku, available, available > 0);
    }

    @Transactional(readOnly = true)
    public List<StockItemDto> getMyStocks(Jwt jwt) {
        UUID supplierId = UUID.fromString(jwt.getSubject());
        return stockItemRepository.findBySupplierId(supplierId).stream()
                .map(this::toStockItemDto)
                .toList();
    }

    @Transactional
    public StockItemDto updateStock(Jwt jwt, String sku, StockUpdateRequest request) {
        StockItem item = stockItemRepository.findBySku(sku)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SKU non trouvé : " + sku));

        // Vérification des droits FOURNISSEUR (propriétaire du SKU)
        boolean isAdmin = jwt.getClaimAsStringList("roles") != null && jwt.getClaimAsStringList("roles").contains("ADMIN");
        if (!isAdmin && item.getSupplierId() != null && !item.getSupplierId().toString().equals(jwt.getSubject())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non autorisé à modifier ce SKU");
        }

        if (request.quantityOnHand() < item.getQuantityReserved()) {
            throw new BusinessConflictException("La quantité physique ne peut être inférieure à la quantité réservée (" + item.getQuantityReserved() + ")");
        }

        int previousOnHand = item.getQuantityOnHand();
        int delta = request.quantityOnHand() - previousOnHand;
        item.setQuantityOnHand(request.quantityOnHand());
        item.setUpdatedAt(Instant.now());
        StockItem saved = stockItemRepository.save(item);

        if (delta != 0) {
            StockMovement movement = new StockMovement();
            movement.setSku(sku);
            movement.setType(MovementType.AJUSTEMENT);
            movement.setQuantity(delta);
            movement.setReferenceId("MAJ_FOURNISSEUR");
            movement.setCreatedAt(Instant.now());
            movementRepository.save(movement);
        }

        checkAlertsAndPublish(saved);
        return toStockItemDto(saved);
    }

    @Transactional
    public StockItemDto updateThreshold(String sku, ThresholdRequest request) {
        StockItem item = stockItemRepository.findBySku(sku)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SKU non trouvé : " + sku));

        item.setReorderThreshold(request.reorderThreshold());
        item.setUpdatedAt(Instant.now());
        StockItem saved = stockItemRepository.save(item);

        checkAlertsAndPublish(saved);
        return toStockItemDto(saved);
    }

    // ==========================================
    // RESERVATIONS REST API (Internal)
    // ==========================================

    @Transactional
    public ReservationDto createReservation(String idempotencyKey, ReservationRequest request) {
        // Idempotence : si déjà existante avec la même clé, renvoie la réservation existante
        Optional<StockReservation> existing = reservationRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            return toReservationDto(existing.get());
        }

        List<String> unavailableSkus = new ArrayList<>();
        Map<String, StockItem> itemsToReserve = new HashMap<>();

        for (ReservationLineDto line : request.lines()) {
            Optional<StockItem> optItem = stockItemRepository.findBySku(line.sku());
            if (optItem.isEmpty() || optItem.get().available() < line.quantity()) {
                unavailableSkus.add(line.sku());
            } else {
                itemsToReserve.put(line.sku(), optItem.get());
            }
        }

        if (!unavailableSkus.isEmpty()) {
            throw new ReservationUnavailableException(unavailableSkus);
        }

        // Toutes les lignes sont disponibles -> réserver
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds((long) request.ttlMinutes() * 60);

        StockReservation reservation = new StockReservation();
        reservation.setReferenceType(request.referenceType());
        reservation.setReferenceId(request.referenceId());
        reservation.setIdempotencyKey(idempotencyKey);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setCreatedAt(now);
        reservation.setExpiresAt(expiresAt);

        for (ReservationLineDto line : request.lines()) {
            StockItem item = itemsToReserve.get(line.sku());
            item.setQuantityReserved(item.getQuantityReserved() + line.quantity());
            item.setUpdatedAt(now);
            stockItemRepository.save(item);

            ReservationLine rLine = new ReservationLine();
            rLine.setSku(line.sku());
            rLine.setQuantity(line.quantity());
            reservation.getLines().add(rLine);

            StockMovement movement = new StockMovement();
            movement.setSku(line.sku());
            movement.setType(MovementType.RESERVATION);
            movement.setQuantity(line.quantity());
            movement.setReferenceId(request.referenceId());
            movement.setCreatedAt(now);
            movementRepository.save(movement);

            checkAlertsAndPublish(item);
        }

        StockReservation saved = reservationRepository.save(reservation);
        return toReservationDto(saved);
    }

    @Transactional(readOnly = true)
    public ReservationDto getReservation(UUID reservationId) {
        StockReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réservation non trouvée"));
        return toReservationDto(reservation);
    }

    @Transactional
    public void releaseReservation(UUID reservationId) {
        StockReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réservation non trouvée"));

        if (reservation.getStatus() == ReservationStatus.CONFIRMEE) {
            throw new BusinessConflictException("Impossible de libérer une réservation déjà confirmée");
        }

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            return; // Déjà libérée ou expirée
        }

        Instant now = Instant.now();
        for (ReservationLine line : reservation.getLines()) {
            stockItemRepository.findBySku(line.getSku()).ifPresent(item -> {
                item.setQuantityReserved(Math.max(0, item.getQuantityReserved() - line.getQuantity()));
                item.setUpdatedAt(now);
                stockItemRepository.save(item);

                StockMovement movement = new StockMovement();
                movement.setSku(line.getSku());
                movement.setType(MovementType.LIBERATION);
                movement.setQuantity(line.getQuantity());
                movement.setReferenceId(reservation.getReferenceId());
                movement.setCreatedAt(now);
                movementRepository.save(movement);

                checkAlertsAndPublish(item);
            });
        }

        reservation.setStatus(ReservationStatus.LIBEREE);
        reservationRepository.save(reservation);
    }

    // ==========================================
    // RESTOCK ALERTS & MOVEMENTS
    // ==========================================

    @Transactional(readOnly = true)
    public List<RestockAlertDto> listAlerts(Jwt jwt, AlertStatus status) {
        boolean isAdmin = jwt.getClaimAsStringList("roles") != null && jwt.getClaimAsStringList("roles").contains("ADMIN");
        if (isAdmin) {
            return (status != null ? alertRepository.findByStatus(status) : alertRepository.findAll())
                    .stream().map(this::toAlertDto).toList();
        } else {
            UUID supplierId = UUID.fromString(jwt.getSubject());
            return (status != null ? alertRepository.findBySupplierIdAndStatus(supplierId, status) : alertRepository.findBySupplierId(supplierId))
                    .stream().map(this::toAlertDto).toList();
        }
    }

    @Transactional
    public RestockAlertDto updateAlert(Jwt jwt, UUID alertId, AlertUpdateRequest request) {
        RestockAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alerte non trouvée"));

        alert.setStatus(request.status());
        if (request.status() == AlertStatus.TRAITEE) {
            alert.setResolvedAt(Instant.now());
        }
        return toAlertDto(alertRepository.save(alert));
    }

    @Transactional(readOnly = true)
    public Page<StockMovementDto> listMovements(String sku, LocalDate from, LocalDate to, Pageable pageable) {
        Instant fromInstant = from != null ? from.atStartOfDay().toInstant(ZoneOffset.UTC) : Instant.EPOCH;
        Instant toInstant = to != null ? to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC) : Instant.now();

        Page<StockMovement> page;
        if (sku != null && !sku.isBlank()) {
            page = movementRepository.findBySkuAndCreatedAtBetween(sku, fromInstant, toInstant, pageable);
        } else {
            page = movementRepository.findByCreatedAtBetween(fromInstant, toInstant, pageable);
        }
        return page.map(m -> new StockMovementDto(m.getId(), m.getSku(), m.getType(), m.getQuantity(), m.getReferenceId(), m.getCreatedAt()));
    }

    // ==========================================
    // KAFKA EVENTS HANDLERS & SCHEDULER
    // ==========================================

    @Transactional
    public void handleVarianteCreee(UUID eventId, String sku, String productId, UUID supplierId) {
        if (isEventAlreadyProcessed(eventId, Topics.CATALOGUE_VARIANTE_CREEE)) {
            return;
        }

        if (stockItemRepository.findBySku(sku).isEmpty()) {
            StockItem item = new StockItem();
            item.setSku(sku);
            item.setProductId(productId);
            item.setSupplierId(supplierId);
            item.setQuantityOnHand(0);
            item.setQuantityReserved(0);
            item.setReorderThreshold(defaultReorderThreshold);
            item.setUpdatedAt(Instant.now());
            stockItemRepository.save(item);
            log.info("StockItem créé pour SKU={}", sku);
        }

        markEventProcessed(eventId, Topics.CATALOGUE_VARIANTE_CREEE);
    }

    @Transactional
    public void handleCommandeValidee(UUID eventId, UUID reservationId, String orderNumber) {
        if (isEventAlreadyProcessed(eventId, Topics.COMMANDE_VALIDEE)) {
            return;
        }

        Optional<StockReservation> optReservation = reservationRepository.findById(reservationId);
        if (optReservation.isPresent()) {
            StockReservation reservation = optReservation.get();
            reservation.setStatus(ReservationStatus.CONFIRMEE);
            reservation.setConfirmedAt(Instant.now());
            reservationRepository.save(reservation);

            Instant now = Instant.now();
            for (ReservationLine line : reservation.getLines()) {
                stockItemRepository.findBySku(line.getSku()).ifPresent(item -> {
                    item.setQuantityOnHand(Math.max(0, item.getQuantityOnHand() - line.getQuantity()));
                    item.setQuantityReserved(Math.max(0, item.getQuantityReserved() - line.getQuantity()));
                    item.setUpdatedAt(now);
                    stockItemRepository.save(item);

                    StockMovement movement = new StockMovement();
                    movement.setSku(line.getSku());
                    movement.setType(MovementType.SORTIE);
                    movement.setQuantity(line.getQuantity());
                    movement.setReferenceId(orderNumber);
                    movement.setCreatedAt(now);
                    movementRepository.save(movement);

                    checkAlertsAndPublish(item);
                });
            }
            log.info("Commande {} validée : réservation {} confirmée et stock décrémenté", orderNumber, reservationId);
        }

        markEventProcessed(eventId, Topics.COMMANDE_VALIDEE);
    }

    @Transactional
    public void handleCommandeAnnulee(UUID eventId, UUID reservationId, String orderNumber) {
        if (isEventAlreadyProcessed(eventId, Topics.COMMANDE_ANNULEE)) {
            return;
        }

        Optional<StockReservation> optReservation = reservationRepository.findById(reservationId);
        if (optReservation.isPresent()) {
            StockReservation reservation = optReservation.get();
            Instant now = Instant.now();

            if (reservation.getStatus() == ReservationStatus.ACTIVE) {
                // Réservation active : libérer la réservation
                for (ReservationLine line : reservation.getLines()) {
                    stockItemRepository.findBySku(line.getSku()).ifPresent(item -> {
                        item.setQuantityReserved(Math.max(0, item.getQuantityReserved() - line.getQuantity()));
                        item.setUpdatedAt(now);
                        stockItemRepository.save(item);

                        StockMovement movement = new StockMovement();
                        movement.setSku(line.getSku());
                        movement.setType(MovementType.LIBERATION);
                        movement.setQuantity(line.getQuantity());
                        movement.setReferenceId(orderNumber);
                        movement.setCreatedAt(now);
                        movementRepository.save(movement);

                        checkAlertsAndPublish(item);
                    });
                }
                reservation.setStatus(ReservationStatus.LIBEREE);
                reservationRepository.save(reservation);
            } else if (reservation.getStatus() == ReservationStatus.CONFIRMEE) {
                // Déjà confirmée : réintégrer dans onHand (ENTREE)
                for (ReservationLine line : reservation.getLines()) {
                    stockItemRepository.findBySku(line.getSku()).ifPresent(item -> {
                        item.setQuantityOnHand(item.getQuantityOnHand() + line.getQuantity());
                        item.setUpdatedAt(now);
                        stockItemRepository.save(item);

                        StockMovement movement = new StockMovement();
                        movement.setSku(line.getSku());
                        movement.setType(MovementType.ENTREE);
                        movement.setQuantity(line.getQuantity());
                        movement.setReferenceId(orderNumber);
                        movement.setCreatedAt(now);
                        movementRepository.save(movement);

                        checkAlertsAndPublish(item);
                    });
                }
            }
        }

        markEventProcessed(eventId, Topics.COMMANDE_ANNULEE);
    }

    @Transactional
    public void handleRetourRecu(UUID eventId, String sku, int quantity, String condition, UUID replacementReservationId) {
        if (isEventAlreadyProcessed(eventId, Topics.RETOUR_RECU)) {
            return;
        }

        Instant now = Instant.now();
        // Réintégrer les articles REVENDABLE
        if ("REVENDABLE".equalsIgnoreCase(condition)) {
            stockItemRepository.findBySku(sku).ifPresent(item -> {
                item.setQuantityOnHand(item.getQuantityOnHand() + quantity);
                item.setUpdatedAt(now);
                stockItemRepository.save(item);

                StockMovement movement = new StockMovement();
                movement.setSku(sku);
                movement.setType(MovementType.RETOUR);
                movement.setQuantity(quantity);
                movement.setReferenceId("RETOUR");
                movement.setCreatedAt(now);
                movementRepository.save(movement);

                checkAlertsAndPublish(item);
            });
        }

        // Échange : confirmer la réservation de remplacement
        if (replacementReservationId != null) {
            reservationRepository.findById(replacementReservationId).ifPresent(reservation -> {
                reservation.setStatus(ReservationStatus.CONFIRMEE);
                reservation.setConfirmedAt(now);
                reservationRepository.save(reservation);

                for (ReservationLine line : reservation.getLines()) {
                    stockItemRepository.findBySku(line.getSku()).ifPresent(item -> {
                        item.setQuantityOnHand(Math.max(0, item.getQuantityOnHand() - line.getQuantity()));
                        item.setQuantityReserved(Math.max(0, item.getQuantityReserved() - line.getQuantity()));
                        item.setUpdatedAt(now);
                        stockItemRepository.save(item);

                        StockMovement movement = new StockMovement();
                        movement.setSku(line.getSku());
                        movement.setType(MovementType.SORTIE);
                        movement.setQuantity(line.getQuantity());
                        movement.setReferenceId("ECHANGE_" + replacementReservationId);
                        movement.setCreatedAt(now);
                        movementRepository.save(movement);

                        checkAlertsAndPublish(item);
                    });
                }
            });
        }

        markEventProcessed(eventId, Topics.RETOUR_RECU);
    }

    @Transactional
    public int releaseExpiredReservations() {
        List<StockReservation> expired = reservationRepository.findByStatusAndExpiresAtBefore(ReservationStatus.ACTIVE, Instant.now());
        Instant now = Instant.now();

        for (StockReservation reservation : expired) {
            for (ReservationLine line : reservation.getLines()) {
                stockItemRepository.findBySku(line.getSku()).ifPresent(item -> {
                    item.setQuantityReserved(Math.max(0, item.getQuantityReserved() - line.getQuantity()));
                    item.setUpdatedAt(now);
                    stockItemRepository.save(item);

                    StockMovement movement = new StockMovement();
                    movement.setSku(line.getSku());
                    movement.setType(MovementType.LIBERATION);
                    movement.setQuantity(line.getQuantity());
                    movement.setReferenceId("EXPIRATION");
                    movement.setCreatedAt(now);
                    movementRepository.save(movement);

                    checkAlertsAndPublish(item);
                });
            }
            reservation.setStatus(ReservationStatus.EXPIREE);
            reservationRepository.save(reservation);
        }
        return expired.size();
    }

    // ==========================================
    // HELPERS & ALERTS
    // ==========================================

    private void checkAlertsAndPublish(StockItem item) {
        int available = item.available();
        if (available <= item.getReorderThreshold()) {
            // Créer une alerte OUVERTE si aucune n'existe déjà
            if (alertRepository.findBySkuAndStatus(item.getSku(), AlertStatus.OUVERTE).isEmpty()) {
                RestockAlert alert = new RestockAlert();
                alert.setSku(item.getSku());
                alert.setSupplierId(item.getSupplierId());
                alert.setAvailableAtAlert(available);
                alert.setThreshold(item.getReorderThreshold());
                alert.setStatus(AlertStatus.OUVERTE);
                alert.setCreatedAt(Instant.now());
                alertRepository.save(alert);
            }
            // Publier E4 StockFaibleEvent
            eventPublisher.publish(Topics.STOCK_FAIBLE, item.getSku(),
                    new StockFaibleEvent(item.getSku(), item.getProductId(), item.getSupplierId(), available, item.getReorderThreshold()));
        } else {
            // Résoudre les alertes ouvertes si disponibles > seuil
            alertRepository.findBySkuAndStatus(item.getSku(), AlertStatus.OUVERTE).ifPresent(alert -> {
                alert.setStatus(AlertStatus.TRAITEE);
                alert.setResolvedAt(Instant.now());
                alertRepository.save(alert);
            });
            // Publier E5 StockReapprovisionneEvent
            eventPublisher.publish(Topics.STOCK_REAPPROVISIONNE, item.getSku(),
                    new StockReapprovisionneEvent(item.getSku(), item.getProductId(), available));
        }
    }

    private boolean isEventAlreadyProcessed(UUID eventId, String topic) {
        return processedEventRepository.existsById(eventId);
    }

    private void markEventProcessed(UUID eventId, String topic) {
        ProcessedEvent pe = new ProcessedEvent();
        pe.setEventId(eventId);
        pe.setTopic(topic);
        pe.setProcessedAt(Instant.now());
        processedEventRepository.save(pe);
    }

    private StockItemDto toStockItemDto(StockItem item) {
        return new StockItemDto(
                item.getSku(),
                item.getProductId(),
                item.getSupplierId(),
                item.getQuantityOnHand(),
                item.getQuantityReserved(),
                item.available(),
                item.getReorderThreshold(),
                item.getUpdatedAt()
        );
    }

    private ReservationDto toReservationDto(StockReservation reservation) {
        List<ReservationLineDto> lines = reservation.getLines().stream()
                .map(l -> new ReservationLineDto(l.getSku(), l.getQuantity()))
                .toList();
        return new ReservationDto(
                reservation.getId(),
                reservation.getReferenceType(),
                reservation.getReferenceId(),
                reservation.getStatus(),
                reservation.getExpiresAt(),
                lines
        );
    }

    private RestockAlertDto toAlertDto(RestockAlert alert) {
        return new RestockAlertDto(
                alert.getId(),
                alert.getSku(),
                alert.getSupplierId(),
                alert.getAvailableAtAlert(),
                alert.getThreshold(),
                alert.getStatus(),
                alert.getCreatedAt()
        );
    }
}
