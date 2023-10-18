package com.example.aucison_service.enums;

public enum PageType {  //pageType Enum
    PRODUCT_DETAIL("상품상세"),
    VIRTUAL_PAYMENT("가상결제페이지"),
    PAYMENT_COMPLETED("결제완료");

    private final String description;

    PageType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
