package com.example.aucison_service.jpa.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductsRepository extends JpaRepository<ProductsEntity, Long> {


    //List<ProductsEntity> findByCategory(String auc);    //이게 맞나?? 잘 모르겠음

//    ProductsEntity findByName(String name);
//
//    List<ProductsEntity> findByCategoryAndKind(String category, String kind);
}
