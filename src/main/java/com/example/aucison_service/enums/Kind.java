package com.example.aucison_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Kind { // 상품 분류(일반/핸드메이드)

    NORM("PROD_NORMAL", "일반 상품"),
    HAND("PROD_HANDMADE", "핸드메이드 상품");

    private final String kind;
    private final String title;
}
