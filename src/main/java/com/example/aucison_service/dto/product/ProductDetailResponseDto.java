package com.example.aucison_service.dto.product;


import com.example.aucison_service.enums.PStatusEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
public class ProductDetailResponseDto {

    //상품을 선택해서 조회했을 때 나오는 상세 정보를 반환하는 dto

    private String name;
    private String kind;
    private String category;
    private String information;
    private String tags;
    private PStatusEnum pStatus;

    //판매자 이메일
    private String email;

    // 경매상품 정보
    private Float startPrice;
    private LocalDateTime end;
    private float high;

    // 비경매상품 정보
    private Float price;

    private Long wishCount; // 추가된 찜 횟수 필드

    private int totCnt;


}
