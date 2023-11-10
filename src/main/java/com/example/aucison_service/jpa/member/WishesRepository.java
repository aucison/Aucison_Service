package com.example.aucison_service.jpa.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishesRepository extends JpaRepository<WishesEntity, Long> {
    List<WishesEntity> findByMembersEntity(MembersEntity membersEntity);
}
