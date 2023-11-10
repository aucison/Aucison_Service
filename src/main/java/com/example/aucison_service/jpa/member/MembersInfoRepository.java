package com.example.aucison_service.jpa.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MembersInfoRepository extends JpaRepository<MembersInfoEntity, Long> {
    MembersInfoEntity findByMembersEntity(MembersEntity membersEntity);
}
