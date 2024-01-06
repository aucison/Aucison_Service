package com.example.aucison_service.elastic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(indexName = "productsdocument")
@Getter
@Setter
@NoArgsConstructor
public class ProductsDocument {
    @Id
    private String id;

    private String name;
    private String kind;
    private String category;
    private String information;
    private String summary;
    private String brand;
    private String email;
    private String img;

    private List<String> images;


    // ProductsEntity의 정보를 반영
    private Long productsId; // ProductsEntity의 ID
    private Long aucEnd; // LocalDateTime을 Long으로 변경
    private Float aucStartPrice; // AucsInfosEntity의 시작 가격
    private Float salePrice; // SaleInfosEntity의 가격


    public ProductsDocument(String id, String name, String kind, String category,
                            String information, String summary, String brand, String email, Long productsId, List<String> images,
                            Long aucEnd, Float aucStartPrice, Float salePrice) {
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.category = category;
        this.information = information;
        this.summary = summary;
        this.brand = brand;
        this.email = email;
        this.productsId = productsId;
        this.images = images;
        this.aucEnd = aucEnd;
        this.aucStartPrice = aucStartPrice;
        this.salePrice = salePrice;
    }
}