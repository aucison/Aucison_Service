package com.example.aucison_service.dto.mypage;

import com.example.aucison_service.enums.Category;
import com.example.aucison_service.jpa.member.Histories;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.swing.plaf.nimbus.State;
import java.util.Date;

@Data
@AllArgsConstructor
public class ResponseOrderHistoryDto {
    private Long historiesId; // 주문 내역 고유 번호
    private String name; // 상품명
    private String info; // 상품 상세 정보
    private String imgUrl; // 이미지//////////////수정필요?
    private Category category; // 경매 여부(경매/비경매)
    private Date ordersAt; // 주문일자////////////수정필요?
    private State state; // 주문 상태(결제/배송)
    private Float price; // 비경매 상품일 때 등록 가격
    private Float nowPrice; // 경매 상품일 때 실시간 가격
    
    public ResponseOrderHistoryDto(Histories histories) {
        this.historiesId = histories.getId();
        this.name = histories.getName();
        this.info = histories.getInfo();
        // imgUrl - HistoriesImg에서 주입
        this.category = histories.getCategory();
        // 주문 일자 - Product Server에서 주입
        // 주문 상태 - Product Server에서 주입
        this.price = histories.getPrice();// 해결 필요
        this.nowPrice = histories.getPrice();// 해결 필요<경매/비경매>구분
    }
}
