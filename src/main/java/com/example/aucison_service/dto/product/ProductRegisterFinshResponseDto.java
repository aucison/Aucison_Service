package com.example.aucison_service.dto.product;


import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProductRegisterFinshResponseDto {

    private String name;
    private String kind;
    private String category;
    private String tags;

    //판매자 이메일
    private String email;

    //이미지 등록
    private String image; // 사용자로부터 받을 이미지 리스트

    // 경매상품 정보
    private Float startPrice;
    private Float high;
    private LocalDateTime end;

    // 비경매상품 정보
    private Float price;
}
