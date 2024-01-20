package com.example.aucison_service.jpa.product.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "bid_counts")
public class BidCountsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="bid_counts_id")
    private Long bidCountsId;

    @Column(name = "products_id")
    private Long productsId;    //일부로 연관관계 없음, products 엔터티의 productsId와 동일

    @Column(name = "tot_cnt")
    private int totCnt; //입찰자 수

}
