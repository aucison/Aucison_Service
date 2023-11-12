package com.example.aucison_service.jpa.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "addresses")
public class AddressesEntity { // 주소

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "addresses_id")
    private Long id; // 주소 id

    @Column(name = "addr_name", nullable = false)
    private String addrName; // 배송지명(사용자 지정값)

    @Column(nullable = false)
    private String zipNum; // 우편번호

    @Column(nullable = false)
    private String addr; // 주소

    @Column(nullable = false)
    private String addrDetail; // 상세주소(동, 호수)

    @Column(nullable = false)
    private String name; // 받는 분 성함
    
    @Column(nullable = false)
    private String tel; // 받는 분 전화번호

    @ManyToOne
    @JoinColumn(name = "members_info_id") // 연관관계 주인
    private MembersInfoEntity membersInfoEntity; // 사용자 추가 정보
}
