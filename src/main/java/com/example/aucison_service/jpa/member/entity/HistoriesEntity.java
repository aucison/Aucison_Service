package com.example.aucison_service.jpa.member.entity;

import com.example.aucison_service.BaseTimeEntity;
import com.example.aucison_service.enums.*;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "histories")
public class HistoriesEntity extends BaseTimeEntity { // 사용자 구매/판매 내역

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "histories_id")
    private Long id; // 사용자 구매/판매 내역 id

    @Column(name = "order_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderType orderType; // 상태 분류(구매/판매)

    @Column(name = "category", nullable = false)
//    @Enumerated(EnumType.STRING)
    private String category; // 경매 여부(경매/비경매)

    @Column(name = "kind", nullable = false)
//    @Enumerated(EnumType.STRING)
    private String kind; // 상품 분류(일반/핸드메이드)

    @Column(name = "products_id", nullable = false)
    private Long productsId;    //상품 id

    @Column(name = "product_name", nullable = false)
    private String productName; // 상품명

    @Column(name = "product_detail", nullable = false)
    private String productDetail; // 상품 상세 정보

    @Column(name = "price", nullable = false)
    private Float price; // 구매/판매 가격
    @Column(name = "orders_id", nullable = true)
    private Long ordersId;    //주문번호

    @ManyToOne
    @JoinColumn(name = "members_info_id") // 연관관계 주인
    private MembersInfoEntity membersInfoEntity; // 사용자 추가 정보

    @OneToOne(mappedBy = "historiesEntity", fetch = FetchType.LAZY) // 양방향 매핑
    private HistoriesImgEntity historiesImg;

    @Builder
    public HistoriesEntity(OrderType orderType, String category, String kind, Long productsId,
                           String productName, String productDetail, Float price, Long ordersId,
                           MembersInfoEntity membersInfoEntity) {
        this.orderType = orderType;
        this.category = category;
        this.kind = kind;
        this.productsId = productsId;
        this.productName = productName;
        this.productDetail = productDetail;
        this.price = price;
        this.ordersId = ordersId;
        this.membersInfoEntity = membersInfoEntity;
    }
}
