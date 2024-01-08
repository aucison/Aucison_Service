package com.example.aucison_service.jpa.member.repository;

import com.example.aucison_service.jpa.member.entity.MembersEntity;
import com.example.aucison_service.jpa.member.entity.WishesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishesRepository extends JpaRepository<WishesEntity, Long> {
    List<WishesEntity> findByMembersEntity(MembersEntity membersEntity);

    Optional<WishesEntity> findByMembersEntityAndProductId(MembersEntity member, Long productId);
    boolean existsByMembersEntityAndProductId(MembersEntity member, Long productId);
}
