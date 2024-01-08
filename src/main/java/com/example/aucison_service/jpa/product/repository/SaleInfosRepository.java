package com.example.aucison_service.jpa.product.repository;

import com.example.aucison_service.jpa.product.entity.ProductsEntity;
import com.example.aucison_service.jpa.product.entity.SaleInfosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleInfosRepository extends JpaRepository<SaleInfosEntity, Long> {
    SaleInfosEntity findByProductsEntity(ProductsEntity products);
}
