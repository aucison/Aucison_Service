package com.example.aucison_service.jpa.product.entity;


import com.example.aucison_service.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "aucs_infos")
public class AucsInfosEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aucs_infos_id")
    private Long aucsInfosId;       //PK


    @Column(name = "start_price", nullable = false)
    private float startPrice;     //경매 시작가


    @Column(name = "highest_price", nullable = false)
    private float highestPrice;     //경매 최고가

    @Column(name = "end", nullable = false)
    private LocalDateTime end;             //경매 종료일


    @Column(name = "bids_code", nullable = false)
    private String bidsCode;       //실시간 가격 식별 코드

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="products_id")
    private ProductsEntity productsEntity;


    @Builder
    public AucsInfosEntity(float startPrice, float highestPrice, LocalDateTime end, String bidsCode, ProductsEntity productsEntity){
        this.startPrice = startPrice;
        this.highestPrice = highestPrice;
        this.end = end;
        this.bidsCode=bidsCode;
        this.productsEntity=productsEntity;
    }

    // 경매 종료 시간을 주어진 분만큼 연장하는 메소드
    public void extendAuctionEndTimeByMinutes(int minutes) {
        if (end != null) {
            this.end = this.end.plusMinutes(minutes);
        }
    }

    public void updateHighestPrice(float highestPrice) {
        if (highestPrice != 0.0f) {
            this.highestPrice = highestPrice;
        }
    }
}
