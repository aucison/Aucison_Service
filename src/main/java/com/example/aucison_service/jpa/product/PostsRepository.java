package com.example.aucison_service.jpa.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostsRepository extends JpaRepository<PostsEntity, Long> {
    List<PostsEntity> findByProductsEntity_ProductsId(Long productId);
}
