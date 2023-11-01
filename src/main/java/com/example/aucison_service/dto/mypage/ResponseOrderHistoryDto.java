package com.example.aucison_service.dto.mypage;

import com.example.aucison_service.enums.Category;
import com.example.aucison_service.enums.OrderStatus;
import com.example.aucison_service.jpa.member.Histories;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.plaf.nimbus.State;
import java.util.Date;

@Data
@Builder
public class ResponseOrderHistoryDto {
    //주문내역 조회 시 사용하는 dto
    private String productName; //상품명
    private String productImg; //상품 사진
    private String productDescription;  //상품 간단 설명
    private String category; // 경매여부(경매/비경매)
    private Long ordersId;  //주문번호
    private String createdTime;  //주문일자(formatter로 인한 string 형 변환)
    private OrderStatus status;  //주문상태

    private Float price; //구매 가격

//    private Long historiesId; // 주문 내역 고유 번호
//    private String name; // 상품명
//    private String info; // 상품 상세 정보
//    private String imgUrl; // 이미지//////////////수정필요?
//    private Category category; // 경매 여부(경매/비경매)
//    private Date ordersAt; // 주문일자////////////수정필요?
//    private State state; // 주문 상태(결제/배송)
//    private Float price; // 비경매 상품일 때 등록 가격
//    private Float nowPrice; // 경매 상품일 때 실시간 가격
//
//    public ResponseOrderHistoryDto(Histories histories) {
//        this.historiesId = histories.getId();
//        this.name = histories.getName();
//        this.info = histories.getInfo();
//        // imgUrl - HistoriesImg에서 주입
//        this.category = histories.getCategory();
//        // 주문 일자 - Product Server에서 주입
//        // 주문 상태 - Product Server에서 주입
//        this.price = histories.getPrice();// 해결 필요
//        this.nowPrice = histories.getPrice();// 해결 필요<경매/비경매>구분
//    }
}
