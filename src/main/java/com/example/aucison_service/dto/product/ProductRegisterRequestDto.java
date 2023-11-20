package com.example.aucison_service.dto.product;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class ProductRegisterRequestDto {



    //상품 등록시 사용하는 Dto
    private String name;
    private String kind;
    private String category;
    //private LocalDateTime createdTime;
    private String information;
    private String summary;
    private String brand;

    //마이크로 서비스간 통신, 판매자 이메일
    private String email;

    //이미지 등록
    private List<MultipartFile> images; // 사용자로부터 받을 이미지 리스트

    // 경매상품 정보
    private Float startPrice;
    private LocalDateTime end;
    private String bidsCode;

    // 비경매상품 정보
    private Float price;
}
