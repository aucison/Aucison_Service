package com.example.aucison_service.jpa.product.repository;

import com.example.aucison_service.jpa.product.entity.AucsInfosEntity;
import com.example.aucison_service.jpa.product.entity.ProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AucsInfosRepository extends JpaRepository<AucsInfosEntity, Long> {
    AucsInfosEntity findByProductsEntity(ProductsEntity products);

    void deleteByProductsId(Long productsId);
}
