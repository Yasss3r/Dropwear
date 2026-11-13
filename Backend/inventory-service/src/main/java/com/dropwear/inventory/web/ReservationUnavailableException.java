package com.dropwear.inventory.web;

import java.util.List;

public class ReservationUnavailableException extends RuntimeException {

    private final List<String> unavailableSkus;

    public ReservationUnavailableException(List<String> unavailableSkus) {
        super("Stock indisponible pour les SKU suivants : " + unavailableSkus);
        this.unavailableSkus = unavailableSkus;
    }

    public List<String> getUnavailableSkus() {
        return unavailableSkus;
    }
}
