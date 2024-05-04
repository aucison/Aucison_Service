package com.example.aucison_service.dto.mypage;

import com.example.aucison_service.enums.Category;
import com.example.aucison_service.enums.Kind;
import com.example.aucison_service.enums.OStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ResponseOrderHistoryDto {
    private Long historiesId;
    private String productName; // 상품명
    private String productImgUrl; // 상품 이미지
    private String category; // 경매 여부(경매/비경매)
    private String kind;  //일반/핸드메이드


    private Long ordersId;  //주문번호
    private Long productsId;
    private String createdTime;  //주문일자(formatter로 인한 string 형 변환)
    private OStatusEnum oStatus;  //주문상태
    private float price; //구매 가격
}
