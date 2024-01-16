package com.example.aucison_service.jpa.product.entity;


import com.example.aucison_service.BaseTimeEntity;
import com.example.aucison_service.enums.PStatusEnum;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "products")
public class ProductsEntity extends BaseTimeEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "products_id")
    private Long productsId;       //PK


    @Column(name = "name", nullable = false)
    private String name;            //상품명

    @Column(name = "kind", nullable = false)
    private String kind;            // 상품분류(일반(NORM) / 핸드메이드(HAND)

    @Column(name = "category", nullable = false)
    private String category;        // 경매여부(경매(AUCS) / 비경매(SALE))

    //    @Column(name = "createdTime", nullable = false)
//    private LocalDateTime createdTime;        //등록 시간 ->이게 주석인게 맞다네??, 상속만 받으면 됨
    @Column(name = "information", nullable = false, columnDefinition = "VARCHAR(10000)")
    private String information;     //상품 정보

    //    @Column(name = "summary", nullable = false)
//    private String summary;         //상품 한줄 설명
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "tags", nullable = true)
    private String tags;           //상품 태그들

    @Column(name = "p_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PStatusEnum pStatus;         //상품 상태


    @OneToOne(mappedBy = "productsEntity",  cascade = CascadeType.ALL)
    private AucsInfosEntity aucsInfosEntity;

    @OneToOne(mappedBy = "productsEntity",  cascade = CascadeType.ALL)
    private SaleInfosEntity saleInfosEntity;

    @OneToMany(mappedBy = "productsEntity")
    List<PostsEntity> postsEntities = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImgEntity> images = new ArrayList<>();

    @Builder
    public ProductsEntity(String name, String kind, String category,
                          String information, String tags, PStatusEnum pStatus, String email) {
        this.name = name;
        this.kind = kind;
        this.category = category;
        this.information = information;
        this.tags = tags;
        this.pStatus = pStatus;
        this.email = email;
    }

    public void addImage(ProductImgEntity image) {
        this.images.add(image);
        image.setProduct(this);
    }

    public void updatePStatus(PStatusEnum pStatus) {  //status 변경
        this.pStatus = pStatus;
    }
}


