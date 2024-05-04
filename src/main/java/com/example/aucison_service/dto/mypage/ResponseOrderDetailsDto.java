package com.example.aucison_service.dto.mypage;


import com.example.aucison_service.enums.Category;
import com.example.aucison_service.enums.Kind;
import com.example.aucison_service.enums.OStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ResponseOrderDetailsDto {
    private String productName; // 상품명
    private String productImgUrl; // 상품 이미지
    private String category; // 경매 여부
    private String kind;

    private Long ordersId; // 주문번호
    private String orderDate; // 주문일자
//    private String endDate; //마감일자
    private OStatusEnum oStatus; // 주문상태
    private float price; // 구매 가격

    // AddressInfo와 BidDetails는 각각의 DTO로 분리할 수도 있습니다.
    private AddressInfo addressInfo; // 배송지 정보
    private List<BidDetails> bidDetails; // 입찰 내역

    @Data
    @AllArgsConstructor
    @Builder
    public static class AddressInfo {
        private String addrName;
        private String recipient;
        private String zipCode;
        private String address;
        private String addressDetail;
        private String contactNumber;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class BidDetails {
        private OStatusEnum bidStatus;
        private LocalDateTime bidTime;
    }
}

