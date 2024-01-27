package com.example.aucison_service.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QStatusEnum {

    GREEN000("일반문의"),
    RED000("신고문의"),
    YELLOW000("오류완료");

    private final String description;
}
