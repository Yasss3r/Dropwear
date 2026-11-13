package com.dropwear.inventory.web;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.dropwear.inventory.domain.AlertStatus;
import com.dropwear.inventory.service.InventoryService;
import com.dropwear.inventory.web.dto.InventoryDtos.*;

/**
 * Contrat d'API /api/inventory/alerts (inventory-service).
 */
@RestController
@RequestMapping("/api/inventory/alerts")
public class AlertController {

    private final InventoryService inventoryService;

    public AlertController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /** GET /api/inventory/alerts — rôle : ADMIN, FOURNISSEUR — codes : 200, 403 */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FOURNISSEUR')")
    public ResponseEntity<List<RestockAlertDto>> listAlerts(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) AlertStatus status) {
        return ResponseEntity.ok(inventoryService.listAlerts(jwt, status));
    }

    /** PATCH /api/inventory/alerts/{alertId} — rôle : ADMIN, FOURNISSEUR — codes : 200, 404 */
    @PatchMapping("/{alertId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FOURNISSEUR')")
    public ResponseEntity<RestockAlertDto> updateAlert(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID alertId,
            @Valid @RequestBody AlertUpdateRequest request) {
        return ResponseEntity.ok(inventoryService.updateAlert(jwt, alertId, request));
    }
}
