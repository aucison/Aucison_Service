package com.example.aucison_service.jpa.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembersRepository extends JpaRepository<MembersEntity, Long> {
    Optional<MembersEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
