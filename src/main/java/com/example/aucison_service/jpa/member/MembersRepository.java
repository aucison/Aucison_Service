package com.example.aucison_service.jpa.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MembersRepository extends JpaRepository<MembersEntity, Long> {
    MembersEntity findByEmail(String email);
}
