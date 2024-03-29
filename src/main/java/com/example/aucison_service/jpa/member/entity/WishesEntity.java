package com.example.aucison_service.jpa.member.entity;

import com.example.aucison_service.jpa.product.entity.PostsEntity;
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
@Table(name = "wishes")
public class WishesEntity { // 찜

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishes_id")
    private Long wishesId; // 찜 id

    @ManyToOne
    @JoinColumn(name = "members_id") // 연관관계 주인
    private MembersEntity membersEntity; // 사용자

    @Column(name = "products_id", nullable = false)
    private Long productId; // products-server: products entity의 products_id

}
