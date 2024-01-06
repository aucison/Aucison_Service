package com.example.aucison_service.jpa.product;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsRepository extends JpaRepository<ProductsEntity, Long> {

    List<ProductsEntity> findByCategoryAndKind(String category, String kind);

    ProductsEntity findByProductsId(Long productsId);
}
