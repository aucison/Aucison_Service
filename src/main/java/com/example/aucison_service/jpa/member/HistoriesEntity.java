package com.example.aucison_service.jpa.member;

import com.example.aucison_service.enums.Category;
import com.example.aucison_service.enums.Kind;
import com.example.aucison_service.enums.OrderType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category; // 경매 여부(경매/비경매)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Kind kind; // 상품 분류(일반/핸드메이드)

    @Column(nullable = false)
    private String name; // 상품명

    @Column(nullable = false)
    private String info; // 상품 상세 정보

    @Column(nullable = false)
    private Float price; // 구매/판매 가격
    @Column(name = "orders_id", nullable = false)
    private Long ordersId;;    //주문번호

    @ManyToOne
    @JoinColumn(name = "members_info_id") // 연관관계 주인
    private MembersInfoEntity membersInfo; // 사용자 추가 정보

    @OneToOne(mappedBy = "histories", fetch = FetchType.LAZY) // 양방향 매핑
    private HistoriesImgEntity historiesImg;
}
