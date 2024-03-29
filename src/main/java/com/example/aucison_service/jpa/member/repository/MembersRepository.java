package com.example.aucison_service.jpa.member.repository;

import com.example.aucison_service.jpa.member.entity.MembersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembersRepository extends JpaRepository<MembersEntity, Long> {
    MembersEntity findByEmail(String email);

    boolean existsByEmail(String email);
}
