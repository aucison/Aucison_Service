package com.example.aucison_service.jpa.shipping;

import com.example.aucison_service.BaseTimeEntity;
import com.example.aucison_service.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "bids")
public class Bids extends BaseTimeEntity { //실시간 응찰 내역
    //필드
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bids_id")
    private Long bidsId; //PK

    @Column(name = "products_id", nullable = false)
    private Long productsId;    //상품 id

    @Column(name = "email", nullable = false)
    private String email;   //이메일
    @Column(name = "now_price", nullable = false)
    private float nowPrice;    //응찰가격

    @Column(name = "status", nullable = false)
    private OrderStatus status;   //응찰 상태(낙찰, 응찰, 패찰, 주문완료)

    @Column(name = "bids_code", nullable = false)
    private String bidsCode;    //응찰 고유 코드

    @Builder
    public Bids(Long productsId, String email,
                float nowPrice, OrderStatus status, String bidsCode) {
        this.productsId = productsId;
        this.email = email;
        this.nowPrice = nowPrice;
        this.status = status;
        this.bidsCode = bidsCode;
    }

    public void updateStatus(OrderStatus status) {  //경매 상품 미낙찰 시 status를 변경하는 메서드
        this.status = status;
    }

}