package com.example.aucison_service.elastic;

import com.example.aucison_service.jpa.product.ProductsEntity;
import com.example.aucison_service.jpa.product.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class ProductsIndexService {
    private final ProductsRepository productsRepository;
    private final ProductsDocumentRepository productsDocumentRepository;

    @Autowired
    public ProductsIndexService(ProductsRepository productsRepository, ProductsDocumentRepository productsDocumentRepository){
        this.productsRepository = productsRepository;
        this.productsDocumentRepository = productsDocumentRepository;
    }

    // 데이터 색인화 메소드
    public void indexProducts() {
        productsRepository.findAll().forEach(productEntity -> {
            ProductsDocument productDocument = convertToDocument(productEntity);
            productsDocumentRepository.save(productDocument);
        });
    }

    // ProductsEntity를 ProductsDocument로 변환하는 메소드
    private ProductsDocument convertToDocument(ProductsEntity productEntity) {
        Float aucStartPrice = null;
        Long aucEndTimestamp = null;
        Float salePrice = null;

        if ("AUCS".equals(productEntity.getCategory()) && productEntity.getAucsInfosEntity() != null) {
            aucStartPrice = productEntity.getAucsInfosEntity().getStartPrice();

            LocalDateTime aucEnd = productEntity.getAucsInfosEntity().getEnd();
            aucEndTimestamp = aucEnd != null ? aucEnd.toInstant(ZoneOffset.UTC).toEpochMilli() : null;
        } else if ("SALE".equals(productEntity.getCategory()) && productEntity.getSaleInfosEntity() != null) {
            salePrice = productEntity.getSaleInfosEntity().getPrice();
        }

        return new ProductsDocument(
                productEntity.getProductsId().toString(),
                productEntity.getName(),
                productEntity.getKind(),
                productEntity.getCategory(),
                productEntity.getInformation(),
                productEntity.getSummary(),
                productEntity.getBrand(),
                productEntity.getEmail(),
                productEntity.getProductsId(),
                aucEndTimestamp,
                aucStartPrice,
                salePrice
        );
    }
}
