package com.example.aucison_service.dto.search;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class ProductSearchResponseDto {

    //검색시 검색 결과 들을 반환하는 Dto

    private Long productsId;
    private String name;
//    private LocalDateTime createdTime;
    private String summary;
    private String brand;

    //마이크로 서비스간 통신
    //private boolean isWished;  // 찜 여부

    private List<String> images;
    // 경매상품 정보
    //private float startPrice;
    private LocalDateTime end;   //end의 경우 사용자나 시스템 로직에 의해 명시적으로 설정해야 하므로 Date형
    private float high;  //최고가 -> 현재 수정한 부분

    // 비경매상품 정보
    private float price;


}
