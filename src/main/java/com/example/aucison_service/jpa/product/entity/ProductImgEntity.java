package com.example.aucison_service.jpa.product.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "productimg")
public class ProductImgEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_img_id")
    private Long productImgId;       //PK

    @Column(name = "url", nullable = false)
    private String url; // 이미지 URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "products_id")
    private ProductsEntity product;

    @Builder
    public ProductImgEntity(String url, ProductsEntity product) {
        this.url = url;
        this.product = product;
    }

    public void setProduct(ProductsEntity product) {
        this.product = product;
    }
}
