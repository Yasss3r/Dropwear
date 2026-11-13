package com.dropwear.inventory.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.dropwear.inventory.events.CommandeAnnuleeEvent;
import com.dropwear.inventory.events.CommandeValideeEvent;
import com.dropwear.inventory.events.EventEnvelope;
import com.dropwear.inventory.events.RetourRecuEvent;
import com.dropwear.inventory.events.Topics;
import com.dropwear.inventory.events.VarianteCreeeEvent;
import com.dropwear.inventory.service.InventoryService;

/**
 * Consommateurs Kafka de inventory-service (consumer group = spring.application.name).
 * Erreur : 3 nouvelles tentatives puis topic .DLT (voir KafkaConfig).
 */
@Component
public class InventoryEventListener {

    private static final Logger log = LoggerFactory.getLogger(InventoryEventListener.class);

    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;

    public InventoryEventListener(ObjectMapper objectMapper, InventoryService inventoryService) {
        this.objectMapper = objectMapper;
        this.inventoryService = inventoryService;
    }

    /** E1 VarianteCreeeEvent (« catalogue.variante-creee ») : crée le StockItem (quantité 0, seuil par défaut 5). */
    @KafkaListener(topics = Topics.CATALOGUE_VARIANTE_CREEE)
    public void onVarianteCreee(String message) throws JsonProcessingException {
        EventEnvelope<VarianteCreeeEvent> envelope = objectMapper.readValue(message, new TypeReference<EventEnvelope<VarianteCreeeEvent>>() { });
        VarianteCreeeEvent payload = envelope.payload();
        log.info("VarianteCreeeEvent reçu : eventId={}, sku={}", envelope.eventId(), payload.sku());
        inventoryService.handleVarianteCreee(envelope.eventId(), payload.sku(), payload.productId(), payload.supplierId());
    }

    /** E2 CommandeValideeEvent (« commande.validee ») : réservation CONFIRMEE : onHand -= q, reserved -= q, mouvement SORTIE (+ E4 si seuil atteint). */
    @KafkaListener(topics = Topics.COMMANDE_VALIDEE)
    public void onCommandeValidee(String message) throws JsonProcessingException {
        EventEnvelope<CommandeValideeEvent> envelope = objectMapper.readValue(message, new TypeReference<EventEnvelope<CommandeValideeEvent>>() { });
        CommandeValideeEvent payload = envelope.payload();
        log.info("CommandeValideeEvent reçu : eventId={}, reservationId={}", envelope.eventId(), payload.reservationId());
        inventoryService.handleCommandeValidee(envelope.eventId(), payload.reservationId(), payload.orderNumber());
    }

    /** E3 CommandeAnnuleeEvent (« commande.annulee ») : réservation LIBEREE (ou réintégration si déjà confirmée). */
    @KafkaListener(topics = Topics.COMMANDE_ANNULEE)
    public void onCommandeAnnulee(String message) throws JsonProcessingException {
        EventEnvelope<CommandeAnnuleeEvent> envelope = objectMapper.readValue(message, new TypeReference<EventEnvelope<CommandeAnnuleeEvent>>() { });
        CommandeAnnuleeEvent payload = envelope.payload();
        log.info("CommandeAnnuleeEvent reçu : eventId={}, reservationId={}", envelope.eventId(), payload.reservationId());
        inventoryService.handleCommandeAnnulee(envelope.eventId(), payload.reservationId(), payload.orderId() != null ? payload.orderId().toString() : "ANNULATION");
    }

    /** E10 RetourRecuEvent (« retour.recu ») : réintègre les articles REVENDABLE + confirme la réservation de remplacement (échange). */
    @KafkaListener(topics = Topics.RETOUR_RECU)
    public void onRetourRecu(String message) throws JsonProcessingException {
        EventEnvelope<RetourRecuEvent> envelope = objectMapper.readValue(message, new TypeReference<EventEnvelope<RetourRecuEvent>>() { });
        RetourRecuEvent payload = envelope.payload();
        log.info("RetourRecuEvent reçu : eventId={}, rmaNumber={}", envelope.eventId(), payload.rmaNumber());
        if (payload.items() != null) {
            for (RetourRecuEvent.Item item : payload.items()) {
                inventoryService.handleRetourRecu(envelope.eventId(), item.sku(), item.quantity(), item.condition(), item.replacementReservationId());
            }
        }
    }
}
