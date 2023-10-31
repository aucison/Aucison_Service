package com.example.aucison_service.jpa.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishesRepository extends JpaRepository<Wishes, Long> {
    List<Wishes> findByMembersEntity(MembersEntity membersEntity);
}
