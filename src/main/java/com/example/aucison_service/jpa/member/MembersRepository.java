package com.example.aucison_service.jpa.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembersRepository extends JpaRepository<MembersEntity, Long> {
    Optional<MembersEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
