package com.example.aucison_service.dto.product;

import com.example.aucison_service.enums.PStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductAllResponseDto {

    private Long productsId;
    private String name;
    private PStatusEnum pStatus;
    private String imageUrl;

    //경매 필요 정보
    private Float startPrice;
    private LocalDateTime end;
    private String bidsCode;
    private Long wishCount; // 추가된 찜 횟수 필드
    private int totCnt;    // 낙찰자 수

    //비경매 필요 정보
    private Float price;

}
