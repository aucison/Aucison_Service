package com.example.aucison_service.dto.home;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductMainResponseDto {
    private Long productId;
    private String name;
    private String kind;
    private String category;
    private String imgUrl;
    private int totCnt; //경매든 비경매든 별도 엔터티로 관리중이라 기본적으로 0값 존재

    // 경매
    private LocalDateTime end;
    private Float high;

    // 비경매
    private Float price;

}
