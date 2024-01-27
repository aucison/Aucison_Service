package com.example.aucison_service.dto.mypage;


import com.example.aucison_service.enums.Category;
import com.example.aucison_service.enums.Kind;
import com.example.aucison_service.enums.OStatusEnum;
import com.example.aucison_service.enums.PStatusEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseSellHistoryDto {
    private String productName; // 상품명
    private String productDescription; // 상품 한줄 설명
    private String productImgUrl; // 상품 이미지
    private String category; // 경매 여부(경매/비경매)
    private String kind;

    private String createdDate; //등록 날짜
    private String soldDate;    //판매 날짜

    private Long ordersId;  //주문번호
//    private OStatusEnum oStatus;  //주문상태
    private PStatusEnum pStatus;  //주문상태
    private float price; //판매 가격
}
