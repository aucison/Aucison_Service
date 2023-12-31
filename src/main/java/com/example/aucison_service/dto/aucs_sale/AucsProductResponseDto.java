package com.example.aucison_service.dto.aucs_sale;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class AucsProductResponseDto {
    //경매상품들 검색 결과 반환시 사용하는 Dto
    private Long productsId;
    private String name;
    //private LocalDateTime createdTime;  // Date 타입에서 LocalDateTime으로 변경
    private String information;
    private String summary;
    private String brand;
    //private boolean isWish;

    private String imageUrl; // 상품 이미지 URL 목록

    private float startPrice;
    private LocalDateTime end;
    private String bidsCode;
}
