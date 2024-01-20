package com.example.aucison_service.jpa.product.repository;

import com.example.aucison_service.jpa.product.entity.CommentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<CommentsEntity, Long> {
    List<CommentsEntity> findByPostsEntity_PostsId(Long posts_id);

}
