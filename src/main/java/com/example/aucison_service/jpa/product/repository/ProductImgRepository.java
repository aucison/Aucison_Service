package com.example.aucison_service.jpa.product.repository;

import com.example.aucison_service.jpa.product.entity.ProductImgEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImgRepository extends JpaRepository<ProductImgEntity, Long> {
    List<ProductImgEntity> findByProductProductsIdOrderByProductImgIdAsc(Long productsId);;
}
