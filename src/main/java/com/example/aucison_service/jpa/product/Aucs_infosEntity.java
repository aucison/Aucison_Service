package com.example.aucison_service.jpa.product;


import com.example.aucison_service.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "auc_infos")
public class Aucs_infosEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aucs_infos_id")
    private Long aucs_infos_id;       //PK


    @Column(name = "start_price", nullable = false)
    private float start_price;     //경매 시작가


    @Column(name = "end", nullable = false)
    private Date end;             //경매 종료일


    @Column(name = "bids_code", nullable = false)
    private String bids_code;       //실시간 가격 식별 코드

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "aucs_infosEntity")
    private ProductsEntity productsEntity;


    @Builder
    public Aucs_infosEntity(float start_price, Date end, String bids_code, ProductsEntity productsEntity){
        this.start_price = start_price;
        this.end = end;
        this.bids_code=bids_code;
        this.productsEntity=productsEntity;
    }
}
