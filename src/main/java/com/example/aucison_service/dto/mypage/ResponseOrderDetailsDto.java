package com.example.aucison_service.dto.mypage;


import com.example.aucison_service.enums.Category;
import com.example.aucison_service.jpa.member.HistoriesEntity;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.swing.plaf.nimbus.State;
import java.util.Date;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ResponseOrderDetailsDto {
    //private Long ordersId; // 주문 고유 번호///////////이거 냅두던지..삭제하던지...수정필요
    private Long historiesId;
    private String prodName; // 상품명
    private String info; // 상품 상세 정보
    private String imgUrl; // 이미지//////////////수정필요?
    private Category category; // 경매 여부(경매/비경매)
    private Date ordersAt; // 주문일자////////////수정필요?
    private State state; // 주문 상태(결제/배송)
    private Float price; // 비경매 상품일 때 등록 가격
    private Float nowPrice; // 경매 상품일 때 실시간 가격
    private String receiver; // 받는 사람
    private String addrName; // 배송지명
    private String addr; // 주소
    private String addrDetail; // 상세주소
    private String tel; // 받는 사람 전화번호
    private Boolean isCompleted; // 배송 완료 여부
    private Boolean isStarted; // 배송 시작 여부
    private List<ResponseBidsHistoryDto> bidsHistory; // 응찰 내역 리스트

    public ResponseOrderDetailsDto(HistoriesEntity historiesEntity) {
        this.historiesId = historiesEntity.getId();
        this.prodName = historiesEntity.getName();
        this.info = historiesEntity.getInfo();
        this.category = historiesEntity.getCategory();
        this.price = historiesEntity.getPrice();// 경매/비경매 이슈 해결
        this.nowPrice = historiesEntity.getPrice();// 경매/비경매 이슈 해결
    }
}
