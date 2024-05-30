package com.example.aucison_service.jpa.product.repository;

import com.example.aucison_service.jpa.product.entity.PostsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PostsRepository extends JpaRepository<PostsEntity, Long> {
    List<PostsEntity> findByProductsEntity_ProductsId(Long productId);

    void deleteByProductsId(Long productsId);
}
