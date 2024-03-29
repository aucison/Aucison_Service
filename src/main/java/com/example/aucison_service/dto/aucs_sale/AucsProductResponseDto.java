package com.example.aucison_service.dto.aucs_sale;


import com.example.aucison_service.enums.PStatusEnum;
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
    private PStatusEnum pStatus;
    private String imageUrl; // 상품 이미지 URL 목록

    private Float startPrice;
    private LocalDateTime end;
    private String bidsCode;

    private Long wishCount; // 추가된 찜 횟수 필드

    private int totCnt;    // 낙찰자 수
}
