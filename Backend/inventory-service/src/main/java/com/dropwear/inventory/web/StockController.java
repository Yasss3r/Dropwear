package com.dropwear.inventory.web;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.dropwear.inventory.service.InventoryService;
import com.dropwear.inventory.web.dto.InventoryDtos.*;

/**
 * Contrat d'API /api/inventory/stocks (inventory-service).
 */
@RestController
@RequestMapping("/api/inventory/stocks")
public class StockController {

    private final InventoryService inventoryService;

    public StockController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /** GET /api/inventory/stocks — rôle : public — codes : 200, 400 */
    @GetMapping
    public ResponseEntity<List<StockDto>> getStocks(@RequestParam List<String> skus) {
        return ResponseEntity.ok(inventoryService.getStocks(skus));
    }

    /** GET /api/inventory/stocks/mine — rôle : FOURNISSEUR — codes : 200, 403 */
    @GetMapping("/mine")
    @PreAuthorize("hasRole('FOURNISSEUR')")
    public ResponseEntity<List<StockItemDto>> myStocks(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(inventoryService.getMyStocks(jwt));
    }

    /** GET /api/inventory/stocks/{sku} — rôle : public — codes : 200, 404 */
    @GetMapping("/{sku}")
    public ResponseEntity<StockDto> getStock(@PathVariable String sku) {
        return ResponseEntity.ok(inventoryService.getStock(sku));
    }

    /** PUT /api/inventory/stocks/{sku} — rôle : FOURNISSEUR, ADMIN — codes : 200, 400, 403, 404, 409 (inférieur au réservé) */
    @PutMapping("/{sku}")
    @PreAuthorize("hasAnyRole('FOURNISSEUR', 'ADMIN')")
    public ResponseEntity<StockItemDto> updateStock(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String sku,
            @Valid @RequestBody StockUpdateRequest request) {
        return ResponseEntity.ok(inventoryService.updateStock(jwt, sku, request));
    }

    /** PATCH /api/inventory/stocks/{sku}/threshold — rôle : ADMIN — codes : 200, 400, 404 */
    @PatchMapping("/{sku}/threshold")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockItemDto> updateThreshold(
            @PathVariable String sku,
            @Valid @RequestBody ThresholdRequest request) {
        return ResponseEntity.ok(inventoryService.updateThreshold(sku, request));
    }
}
