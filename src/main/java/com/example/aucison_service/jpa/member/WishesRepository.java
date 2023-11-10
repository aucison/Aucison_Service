package com.example.aucison_service.jpa.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishesRepository extends JpaRepository<WishesEntity, Long> {
    List<WishesEntity> findByMembersEntity(MembersEntity membersEntity);
}
