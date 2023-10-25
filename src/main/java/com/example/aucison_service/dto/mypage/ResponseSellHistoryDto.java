package com.example.aucison_service.dto.mypage;


import com.example.aucison_service.enums.Category;
import com.example.aucison_service.jpa.member.Histories;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.swing.plaf.nimbus.State;

@Data
@AllArgsConstructor
public class ResponseSellHistoryDto {
    //private Long ordersId; // 주문 고유 번호//////수정필요
    private Long historiesId;
    private String name; // 상품명
    private String info; // 상품 상세 정보
    private String imgUrl; // 이미지//////////////수정필요?
    private Category category; // 경매 여부(경매/비경매)
    private State state; // 주문 상태(결제/배송)
    private Float price; // 비경매 상품일 때 등록 가격
    private Float nowPrice; // 경매 상품일 때 실시간 가격

    public ResponseSellHistoryDto(Histories histories) {
        this.historiesId = histories.getId();
        this.name = histories.getName();
        this.info = histories.getInfo();
        this.category = histories.getCategory();
        this.price = histories.getPrice(); ////경매/비경매 개선 필요
        this.nowPrice = histories.getPrice(); ////경매/비경매 개선 필요
    }
}
