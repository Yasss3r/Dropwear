package com.dropwear.inventory.service;

import java.time.Instant;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dropwear.inventory.domain.*;
import com.dropwear.inventory.messaging.EventPublisher;
import com.dropwear.inventory.repository.*;
import com.dropwear.inventory.web.ReservationUnavailableException;
import com.dropwear.inventory.web.dto.InventoryDtos.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private StockItemRepository stockItemRepository;
    @Mock
    private StockReservationRepository reservationRepository;
    @Mock
    private StockMovementRepository movementRepository;
    @Mock
    private RestockAlertRepository alertRepository;
    @Mock
    private ProcessedEventRepository processedEventRepository;
    @Mock
    private EventPublisher eventPublisher;

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(
                stockItemRepository,
                reservationRepository,
                movementRepository,
                alertRepository,
                processedEventRepository,
                eventPublisher,
                5
        );
    }

    @Test
    @DisplayName("getStock - retourne le disponible et inStock=true")
    void getStock_shouldReturnStockDto() {
        StockItem item = new StockItem();
        item.setSku("HOOD-BOXY-BLK-M");
        item.setQuantityOnHand(40);
        item.setQuantityReserved(5);

        when(stockItemRepository.findBySku("HOOD-BOXY-BLK-M")).thenReturn(Optional.of(item));

        StockDto result = inventoryService.getStock("HOOD-BOXY-BLK-M");

        assertThat(result.sku()).isEqualTo("HOOD-BOXY-BLK-M");
        assertThat(result.available()).isEqualTo(35);
        assertThat(result.inStock()).isTrue();
    }

    @Test
    @DisplayName("createReservation - succès quand stock suffisant")
    void createReservation_successWhenStockAvailable() {
        StockItem item = new StockItem();
        item.setSku("TSHIRT-OVS-WHT-L");
        item.setQuantityOnHand(20);
        item.setQuantityReserved(2);

        when(reservationRepository.findByIdempotencyKey("key-123")).thenReturn(Optional.empty());
        when(stockItemRepository.findBySku("TSHIRT-OVS-WHT-L")).thenReturn(Optional.of(item));
        when(reservationRepository.save(any(StockReservation.class))).thenAnswer(invocation -> {
            StockReservation r = invocation.getArgument(0);
            r.setId(UUID.randomUUID());
            return r;
        });

        ReservationRequest request = new ReservationRequest(
                ReservationType.COMMANDE,
                "ord_101",
                List.of(new ReservationLineDto("TSHIRT-OVS-WHT-L", 3)),
                30
        );

        ReservationDto dto = inventoryService.createReservation("key-123", request);

        assertThat(dto.status()).isEqualTo(ReservationStatus.ACTIVE);
        assertThat(item.getQuantityReserved()).isEqualTo(5);
        verify(movementRepository).save(any(StockMovement.class));
    }

    @Test
    @DisplayName("createReservation - lance ReservationUnavailableException si stock insuffisant")
    void createReservation_throwsWhenStockInsufficient() {
        StockItem item = new StockItem();
        item.setSku("TSHIRT-OVS-WHT-L");
        item.setQuantityOnHand(4);
        item.setQuantityReserved(2); // disponible = 2

        when(reservationRepository.findByIdempotencyKey("key-999")).thenReturn(Optional.empty());
        when(stockItemRepository.findBySku("TSHIRT-OVS-WHT-L")).thenReturn(Optional.of(item));

        ReservationRequest request = new ReservationRequest(
                ReservationType.COMMANDE,
                "ord_102",
                List.of(new ReservationLineDto("TSHIRT-OVS-WHT-L", 5)), // demande 5
                30
        );

        assertThatThrownBy(() -> inventoryService.createReservation("key-999", request))
                .isInstanceOf(ReservationUnavailableException.class);

        verify(reservationRepository, never()).save(any(StockReservation.class));
    }

    @Test
    @DisplayName("handleCommandeValidee - décrémente physique et réservé")
    void handleCommandeValidee_shouldDeductStock() {
        UUID eventId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();

        StockItem item = new StockItem();
        item.setSku("HOOD-BOXY-BLK-M");
        item.setQuantityOnHand(10);
        item.setQuantityReserved(2);

        StockReservation reservation = new StockReservation();
        reservation.setId(reservationId);
        reservation.setStatus(ReservationStatus.ACTIVE);
        ReservationLine line = new ReservationLine();
        line.setSku("HOOD-BOXY-BLK-M");
        line.setQuantity(2);
        reservation.setLines(List.of(line));

        when(processedEventRepository.existsById(eventId)).thenReturn(false);
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(stockItemRepository.findBySku("HOOD-BOXY-BLK-M")).thenReturn(Optional.of(item));

        inventoryService.handleCommandeValidee(eventId, reservationId, "ord_1001");

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMEE);
        assertThat(item.getQuantityOnHand()).isEqualTo(8);
        assertThat(item.getQuantityReserved()).isEqualTo(0);
        verify(movementRepository).save(any(StockMovement.class));
        verify(processedEventRepository).save(any(ProcessedEvent.class));
    }
}
