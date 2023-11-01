package com.example.aucison_service.enums;

public enum OrderType {
    BUY("구매"),
    SELL("판매");

    private final String description;

    OrderType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
