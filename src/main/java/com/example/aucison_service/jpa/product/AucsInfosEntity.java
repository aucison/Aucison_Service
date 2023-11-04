package com.example.aucison_service.jpa.product;


import com.example.aucison_service.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "aucs_infos")
public class AucsInfosEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aucs_infos_id")
    private Long aucsInfosId;       //PK


    @Column(name = "start_price", nullable = false)
    private float startPrice;     //경매 시작가


    @Column(name = "end", nullable = false)
    private Date end;             //경매 종료일


    @Column(name = "bids_code", nullable = false)
    private String bidsCode;       //실시간 가격 식별 코드

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "aucsInfosEntity")
    private ProductsEntity productsEntity;


    @Builder
    public AucsInfosEntity(float startPrice, Date end, String bidsCode, ProductsEntity productsEntity){
        this.startPrice = startPrice;
        this.end = end;
        this.bidsCode=bidsCode;
        this.productsEntity=productsEntity;
    }
}
