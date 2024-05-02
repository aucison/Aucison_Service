package com.example.aucison_service.dto.mypage;


import com.example.aucison_service.enums.Category;
import com.example.aucison_service.enums.Kind;
import com.example.aucison_service.enums.OStatusEnum;
import com.example.aucison_service.enums.PStatusEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
//마이페이지 메인에 보이는 판매목록 조회 용
public class ResponseSellHistoryDto {
    private Long historiesId;
    private String productName; // 상품명
    private String productImgUrl; // 상품 이미지

    private String category; // 경매 여부(경매/비경매)
    private String kind;

    private String createdDate; //등록 날짜
    private PStatusEnum pStatus;  //주문상태
    private Long productsId;
    private float price; //판매 가격
}
