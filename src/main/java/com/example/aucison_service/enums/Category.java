package com.example.aucison_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category { // 경매 분류(경매/비경매)

    AUC("PROD_AUCTION", "경매 상품"),
    NOR("PROD_NORMAL", "비경매 상품");

    private final String kind;
    private final String title;
}
