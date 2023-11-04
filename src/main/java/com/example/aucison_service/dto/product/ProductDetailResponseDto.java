package com.example.aucison_service.dto.product;


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
    private LocalDateTime createdTime;
    private String information;
    private String summary;
    private String brand;



    // 경매상품 정보
    private float startPrice;
    private Date end;
    private String bidsCode;

    // 비경매상품 정보
    private float price;


    //게시물
    //아직 미작성

    //댓글
    //아직 미작성

    //미작성 이유 ->  postserviceimpl에서 별도로 따로 서비스를 구현할 수 있기 때문

}
