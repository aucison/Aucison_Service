package com.example.aucison_service.jpa.shipping;

import com.example.Aucison_Shipping_Service.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "refunds")
public class Refunds extends BaseTimeEntity {  //환불
    //필드
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refundsId")
    private Long refundsId; //PK

    @Column(name = "cost", nullable = false)
    private float cost;    //환불해줄 금액

    @ManyToOne
    @JoinColumn(name = "orders_id")
    private Orders orders;  //주문 정보

    @Builder
    public Refunds(float cost) {
        this.cost = cost;
    }
}
