package com.example.aucison_service.dto.aucs_sale;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SaleProductResponseDto {

    //비 경매상품(일반)들 검색 결과 반환시 사용하는 Dto
    private Long productsId;
    private String name;
    private String status;
    private String imageUrl;

    private float price;

    private Long wishCount; // 추가된 찜 횟수 필드
}
