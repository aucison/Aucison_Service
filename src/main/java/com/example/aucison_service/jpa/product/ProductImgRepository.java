package com.example.aucison_service.jpa.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImgRepository extends JpaRepository<ProductImgEntity, Long> {
    List<ProductImgEntity> findByProductProductsIdOrderByProductImgIdAsc(Long productsId);;
}
