package com.example.aucison_service.jpa.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<CommentsEntity, Long> {
    List<CommentsEntity> findByPostsId(Long posts_id);
}
