package com.example.aucison_service.jpa.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembersInfoRepository extends JpaRepository<MembersInfoEntity, Long> {
    MembersInfoEntity findByMembersEntity(MembersEntity membersEntity);
}
