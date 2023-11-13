package com.example.aucison_service.dto.mypage;


import com.example.aucison_service.enums.Category;
import com.example.aucison_service.enums.OrderStatus;
import com.example.aucison_service.jpa.member.HistoriesEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.swing.plaf.nimbus.State;

@Data
@Builder
public class ResponseSellHistoryDto {
    private String productName; // 상품명
    private String productDescription; // 상품 한줄 설명
    private String productImgUrl; // 상품 이미지
    private Category category; // 경매 여부(경매/비경매)

    private String createdDate; //등록 날짜
    private String soldDate;    //판매 날짜

    private Long ordersId;  //주문번호
    private OrderStatus status;  //주문상태
    private float price; //판매 가격
}
