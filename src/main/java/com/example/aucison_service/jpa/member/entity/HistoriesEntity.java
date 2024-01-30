package com.example.aucison_service.jpa.member.entity;

import com.example.aucison_service.BaseTimeEntity;
import com.example.aucison_service.enums.*;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "histories")
public class HistoriesEntity { // 사용자 구매/판매 내역

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "histories_id")
    private Long id; // 사용자 구매/판매 내역 id

    @Column(name = "order_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderType orderType; // 상태 분류(구매/판매)

    @Column(name = "products_id", nullable = false)
    private Long productsId;    //상품 id

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "sold_date", nullable = true)
    private LocalDateTime soldDate;

    @Column(name = "orders_id", nullable = true)
    private Long ordersId;    //주문번호

    @ManyToOne
    @JoinColumn(name = "members_info_id") // 연관관계 주인
    private MembersInfoEntity membersInfoEntity; // 사용자 추가 정보

    @OneToOne(mappedBy = "historiesEntity", fetch = FetchType.LAZY) // 양방향 매핑
    private HistoriesImgEntity historiesImg;

    @Builder
    public HistoriesEntity(OrderType orderType, Long productsId, String email,
                           LocalDateTime soldDate, Long ordersId, MembersInfoEntity membersInfoEntity) {
        this.orderType = orderType;
        this.productsId = productsId;
        this.email = email;
        this.soldDate = soldDate;
        this.ordersId = ordersId;
        this.membersInfoEntity = membersInfoEntity;
    }

    public void updateSoldDate(LocalDateTime soldDate) {
        this.soldDate = soldDate;
    }
}
