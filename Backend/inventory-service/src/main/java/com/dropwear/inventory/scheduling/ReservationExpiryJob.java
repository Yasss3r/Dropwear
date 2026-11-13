package com.dropwear.inventory.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dropwear.inventory.service.InventoryService;

/**
 * Verrouillage temporaire : libère chaque minute les réservations ACTIVE dont expiresAt est dépassé
 * (compensation automatique si la commande n'aboutit pas). Remplace un TTL Redis.
 */
@Component
public class ReservationExpiryJob {

    private static final Logger log = LoggerFactory.getLogger(ReservationExpiryJob.class);

    private final InventoryService inventoryService;

    public ReservationExpiryJob(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Scheduled(fixedRate = 60_000)
    public void releaseExpiredReservations() {
        int released = inventoryService.releaseExpiredReservations();
        if (released > 0) {
            log.info("{} réservation(s) expirée(s) libérée(s)", released);
        }
    }
}
