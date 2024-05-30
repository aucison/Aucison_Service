package com.example.aucison_service.jpa.product.repository;

import com.example.aucison_service.jpa.product.entity.CommentsEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<CommentsEntity, Long> {
    List<CommentsEntity> findByPostsEntity_PostsId(Long posts_id);

    @Modifying
    @Query("DELETE FROM CommentsEntity c WHERE c.postsEntity.productsEntity.productsId = :productsId")
    void deleteByProductsEntity_ProductsId(@Param("productsId") Long productsId);
}
