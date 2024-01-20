package com.example.aucison_service.jpa.product.repository;

import com.example.aucison_service.jpa.product.entity.ProductsEntity;
import com.example.aucison_service.jpa.product.entity.SaleInfosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleInfosRepository extends JpaRepository<SaleInfosEntity, Long> {
    SaleInfosEntity findByProductsEntity(ProductsEntity products);
}
