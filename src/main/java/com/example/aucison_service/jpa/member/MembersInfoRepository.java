package com.example.aucison_service.jpa.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MembersInfoRepository extends JpaRepository<MembersInfo, Long> {
    MembersInfo findByMembers(MembersEntity membersEntity);
}
