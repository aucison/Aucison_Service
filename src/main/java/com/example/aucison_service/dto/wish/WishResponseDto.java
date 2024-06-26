package com.example.aucison_service.dto.wish;



import com.example.aucison_service.enums.PStatusEnum;
import lombok.Builder;

import lombok.Data;


import java.time.LocalDateTime;

@Builder
@Data
public class WishResponseDto {

    private Long wishesId; // 찜 ID
    private String name; // 상품명
    private String category; // 경매 여부(경매/비경매)
    private String kind;
    private String tags;
    private PStatusEnum pStatus;


    //경매상품
    private float price; // 현재 응찰 최고 가격
    private LocalDateTime end;


    //비경매상품
    private float nowPrice; // 실시간 가격(경매)

    // 추가 필드
    private String imageUrl; // 이미지 URL
    private Long productsId; // 상품 ID
}
