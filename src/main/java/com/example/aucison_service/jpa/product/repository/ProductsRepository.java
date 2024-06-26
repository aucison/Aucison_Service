package com.example.aucison_service.jpa.product.repository;

import com.example.aucison_service.jpa.product.entity.ProductsEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Repository
public interface ProductsRepository extends JpaRepository<ProductsEntity, Long> {

    List<ProductsEntity> findByCategoryAndKind(String category, String kind);

    ProductsEntity findByProductsId(Long productsId);


    List<ProductsEntity> findTop10ByOrderByCreatedDateDesc();

    void deleteByProductsId(Long productsId);

    @Query("SELECT p FROM ProductsEntity p WHERE p.aucsInfosEntity.end < :now")
    List<ProductsEntity> findByAuctionExpiryDateBefore(@Param("now") LocalDateTime now);
}