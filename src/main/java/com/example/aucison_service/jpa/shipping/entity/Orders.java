package com.example.aucison_service.jpa.shipping.entity;

import com.example.aucison_service.BaseTimeEntity;
import com.example.aucison_service.enums.OStatusEnum;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "orders")
public class Orders extends BaseTimeEntity {   //주문 정보
    //필드
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id")
    private Long ordersId; //PK, 주문번호
    @Column(name = "products_id", nullable = false)
    private Long productsId;    //상품 id
    @Column(name = "members_code", nullable = false)
    private String email;   //구매자 이메일

    @Column(name = "o_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OStatusEnum oStatus;   //주문상태(낙찰, 응찰, 패찰, 주문완료)

    @OneToOne(mappedBy = "orders")
    private Payments payments;
    @OneToOne(mappedBy = "orders")
    private Deliveries deliveries;

    @Builder
    public Orders(Long productsId, String email,
                  OStatusEnum oStatus) {
        this.productsId = productsId;
        this.email = email;
//        this.createdTime = createdTime;
        this.oStatus = oStatus;
    }

    public void updateStatus(OStatusEnum oStatus) {  //status 변경
        this.oStatus = oStatus;
    }
}