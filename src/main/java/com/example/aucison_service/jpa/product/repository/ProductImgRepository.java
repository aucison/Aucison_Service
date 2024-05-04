package com.example.aucison_service.jpa.product.repository;

import com.example.aucison_service.jpa.product.entity.ProductImgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductImgRepository extends JpaRepository<ProductImgEntity, Long> {
    List<ProductImgEntity> findByProductProductsIdOrderByProductImgIdAsc(Long productsId);;

    void deleteByProduct_ProductsId(Long productsId);
}
