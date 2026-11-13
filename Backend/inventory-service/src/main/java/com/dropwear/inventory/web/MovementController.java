package com.dropwear.inventory.web;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.dropwear.inventory.service.InventoryService;
import com.dropwear.inventory.web.dto.InventoryDtos.StockMovementDto;

/**
 * Contrat d'API /api/inventory/movements (inventory-service).
 */
@RestController
@RequestMapping("/api/inventory/movements")
public class MovementController {

    private final InventoryService inventoryService;

    public MovementController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /** GET /api/inventory/movements — rôle : ADMIN — codes : 200 */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<StockMovementDto>> listMovements(
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Pageable pageable) {
        return ResponseEntity.ok(inventoryService.listMovements(sku, from, to, pageable));
    }
}
