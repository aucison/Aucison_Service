package com.example.aucison_service.jpa.shipping.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "deliveries")
public class Deliveries {   //배송
    //필드
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deliveries_id")
    private Long deliveriesId; //PK
    @Column(name = "addr_name", nullable = false)
    private String addrName;    //배송지명
    @Column(name = "zip_num", nullable = false)
    private String zipNum;    //우편번호
    @Column(name = "addr", nullable = false)
    private String addr;    //주소
    @Column(name = "addr_detail", nullable = false)
    private String addrDetail;    //상세주소(동,호수)
    @Column(name = "name", nullable = false)
    private String name;    //받는분 이름
    @Column(name = "tel", nullable = false)
    private String tel;    //받는분 전화번호

    @Column(name = "is_completed")
    private boolean isCompleted;    //배송완료 여부

    @Column(name = "is_started")
    private boolean isStarted;    //배송시작 여부

    //외래키
    @OneToOne
    @JoinColumn(name = "orders_id")
    private Orders orders;

    @Builder
    public Deliveries(String addrName,
                      String zipNum, String addr, String addrDetail,
                      String name, String tel,
                      boolean isCompleted, boolean isStarted,
                      Orders orders) {
        this.addrName = addrName;
        this.zipNum = zipNum;
        this.addr = addr;
        this.addrDetail = addrDetail;
        this.name = name;
        this.tel = tel;
        this.isCompleted = isCompleted;
        this.isStarted = isStarted;
        this.orders = orders;
    }

    public void completeDelivery() {    //배송완료
        this.isCompleted = true;
    }

    public void startDelivery() {   //배송시작
        this.isStarted = true;
    }
}