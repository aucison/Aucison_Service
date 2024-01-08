package com.example.aucison_service.jpa.member.entity;

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
@Table(name = "histories_img")
public class HistoriesImgEntity { // 상품 사진

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "histories_imgs_id")
    private Long id; // 사용자 프로필 사진 id

    @Column(nullable = false)
    private String url; // 이미지 경로

    @OneToOne
    @JoinColumn(name = "histories_id") // 연관관계 주인
    private HistoriesEntity historiesEntity; // 사용자 구매/판매 내역
}
