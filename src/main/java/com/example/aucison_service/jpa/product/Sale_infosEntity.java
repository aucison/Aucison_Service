package com.example.aucison_service.jpa.product;


import com.example.aucison_service.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "nor_infos")
public class Sale_infosEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_infos_id")
    private Long sale_infos_id;       //PK


    @Column(name = "price", nullable = false)
    private float price;     //등록 가격


    @OneToOne(fetch = FetchType.LAZY, mappedBy = "sale_infosEntity")
    private ProductsEntity productsEntity;

    @Builder
    public Sale_infosEntity(float price, ProductsEntity productsEntity) {
        this.price = price;
        this.productsEntity=productsEntity;
    }
}
