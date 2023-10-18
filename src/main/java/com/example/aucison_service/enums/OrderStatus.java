package com.example.aucison_service.enums;

public enum OrderStatus {  //status ENUM
    WINNING_BID("낙찰"),
    WAITING_FOR_BID("응찰"),
    FAILED_BID("패찰"),
    ORDER_COMPLETED("주문완료");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}