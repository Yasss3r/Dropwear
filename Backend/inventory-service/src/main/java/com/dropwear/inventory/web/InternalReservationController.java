package com.dropwear.inventory.web;

import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.dropwear.inventory.service.InventoryService;
import com.dropwear.inventory.web.dto.InventoryDtos.*;

/**
 * Contrat d'API /internal/inventory/reservations (inventory-service).
 */
@RestController
@RequestMapping("/internal/inventory/reservations")
public class InternalReservationController {

    private final InventoryService inventoryService;

    public InternalReservationController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /** POST /internal/inventory/reservations — rôle : SERVICE — codes : 201, 200 (rejeu idempotent), 400, 409 {unavailableSkus} — appelé par S2 et S4 */
    @PostMapping
    @PreAuthorize("hasRole('SERVICE')")
    public ResponseEntity<ReservationDto> reserve(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody ReservationRequest request) {
        ReservationDto dto = inventoryService.createReservation(idempotencyKey, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /** GET /internal/inventory/reservations/{reservationId} — rôle : SERVICE — codes : 200, 404 */
    @GetMapping("/{reservationId}")
    @PreAuthorize("hasRole('SERVICE')")
    public ResponseEntity<ReservationDto> getReservation(@PathVariable UUID reservationId) {
        return ResponseEntity.ok(inventoryService.getReservation(reservationId));
    }

    /** DELETE /internal/inventory/reservations/{reservationId} — rôle : SERVICE — codes : 204, 404, 409 (déjà confirmée) */
    @DeleteMapping("/{reservationId}")
    @PreAuthorize("hasRole('SERVICE')")
    public ResponseEntity<Void> release(@PathVariable UUID reservationId) {
        inventoryService.releaseReservation(reservationId);
        return ResponseEntity.noContent().build();
    }
}
