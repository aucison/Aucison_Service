package com.example.aucison_service.jpa.shipping;

import com.example.aucison_service.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "payments")
public class Payments extends BaseTimeEntity { //결제
    //필드
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payments_id")
    private Long paymentsId; //PK

    @Column(name = "cost", nullable = false)
    private float cost;    //결제 금액
//    @Column(name = "created_time", nullable = false)
//    private LocalDateTime createdTime;            //결제 승인 시점

    //외래키
    @OneToOne
    @JoinColumn(name = "orders_id")
    private Orders orders;


    @Builder
    public Payments(float cost, Orders orders) {
        this.cost = cost;
        this.orders = orders;
    }
}