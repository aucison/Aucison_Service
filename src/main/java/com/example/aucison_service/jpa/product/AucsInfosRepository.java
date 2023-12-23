package com.example.aucison_service.jpa.product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AucsInfosRepository extends JpaRepository<AucsInfosEntity, Long> {
    AucsInfosEntity findByProductsEntity(ProductsEntity products);
}
