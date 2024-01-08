package com.example.aucison_service.jpa.product.repository;

import com.example.aucison_service.jpa.product.entity.CommentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<CommentsEntity, Long> {
    List<CommentsEntity> findByPostsEntity_PostsId(Long posts_id);

}
