package com.example.aucison_service.jpa.product.repository;

import com.example.aucison_service.jpa.member.entity.InquirysEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InquirysRepository extends JpaRepository<InquirysEntity, Long> {
}
