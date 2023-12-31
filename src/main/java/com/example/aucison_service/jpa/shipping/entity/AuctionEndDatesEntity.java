package com.example.aucison_service.jpa.shipping.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "auction_end_dates")
public class AuctionEndDatesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auction_end_dates_id")
    private Long auctionEndDatesId; // PK

    @Column(name = "products_id", nullable = false, unique = true)
    private Long productsId; // 상품 ID, Unique 제약조건 추가

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate; // 경매 종료일

    @Builder
    public AuctionEndDatesEntity(Long productsId, LocalDateTime endDate) {
        this.productsId = productsId;
        this.endDate = endDate;
    }
}

