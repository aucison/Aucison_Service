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
public class HistoriesEntity extends BaseTimeEntity{ // 사용자 구매/판매 내역

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "histories_id")
    private Long historiesId; // 사용자 구매/판매 내역 id

    @Column(name = "order_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderType orderType; // 상태 분류(구매/판매)

    //추가
    @Column(name = "name", nullable = false)
    private String name;            //상품명

    //추가
    @Column(name = "kind", nullable = false)
    private String kind;            // 상품분류(일반(NORM) / 핸드메이드(HAND)

    //추가
    @Column(name = "category", nullable = false)
    private String category;        // 경매여부(경매(AUCS) / 비경매(SALE))

    @Column(name = "highest_price")
    private float highestPrice;     //최고가 (구매-경매를 위함)

    @Column(name = "salePrice")
    private float salePrice;     //최고가 (구매-경매를 위함)


    @Column(name = "p_status")
    @Enumerated(EnumType.STRING)
    private PStatusEnum pStatus;

    @Column(name = "o_status")
    @Enumerated(EnumType.STRING)
    private OStatusEnum oStatus;
    @Column(name = "orders_id", nullable = true)
    private Long ordersId;      //주문번호

    @Column(name = "products_id", nullable = true)
    private Long productsId;    //상품번호

    @ManyToOne
    @JoinColumn(name = "members_info_id") // 연관관계 주인
    private MembersInfoEntity membersInfoEntity; // 사용자 추가 정보

    @OneToOne(mappedBy = "historiesEntity", fetch = FetchType.LAZY) // 양방향 매핑
    private HistoriesImgEntity historiesImg;

    @Builder
    public HistoriesEntity(OrderType orderType, String name, String category, String kind,
                           float highestPrice, float salePrice, PStatusEnum pStatus, OStatusEnum oStatus,
                           Long ordersId, Long productsId, MembersInfoEntity membersInfoEntity) {
        this.orderType = orderType;
        this.name = name;
        this.category = category;
        this.kind = kind;
        this.highestPrice = highestPrice;
        this.salePrice = salePrice;
        this.pStatus = pStatus;
        this.oStatus = oStatus;
        this.ordersId = ordersId;
        this.productsId = productsId;
        this.membersInfoEntity = membersInfoEntity;
    }

//    public void updateSoldDate(LocalDateTime soldDate) {
//        this.soldDate = soldDate;
//    }

    public void updatePstatus(PStatusEnum pStatus) {
        this.pStatus = pStatus;
    }
    public void updateOstatus(OStatusEnum oStatus) {
        this.oStatus = oStatus;
    }
}
