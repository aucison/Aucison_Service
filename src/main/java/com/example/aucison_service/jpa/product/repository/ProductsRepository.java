package com.example.aucison_service.jpa.product.repository;

import com.example.aucison_service.jpa.product.entity.ProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Repository
public interface ProductsRepository extends JpaRepository<ProductsEntity, Long> {

    List<ProductsEntity> findByCategoryAndKind(String category, String kind);

    ProductsEntity findByProductsId(Long productsId);


    List<ProductsEntity> findTop10ByOrderByCreatedDateDesc();

    void deleteByProductId(Long productId);

    List<ProductsEntity> findExpiredAuctions(LocalDateTime now);
}
