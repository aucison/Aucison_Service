package com.example.aucison_service.enums;

import lombok.Getter;

@Getter
public enum OStatusEnum {
    S000("판매중"),
    B000("응찰중"),
    B001("최고가 입찰"),
    B002("응찰취소"),
    C000("구매완료"),
    C001("낙찰"),
    C002("패찰");

    private final String description;

    OStatusEnum(String description) {
        this.description = description;
    }
}
