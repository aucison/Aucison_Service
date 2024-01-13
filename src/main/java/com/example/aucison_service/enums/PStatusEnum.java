package com.example.aucison_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PStatusEnum {
    S000("판매중"),
    B000("응찰중"),
    C000("판매완료"),
    Z999("특수");

    private final String description;

}
