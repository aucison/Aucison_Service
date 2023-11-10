package com.example.aucison_service.jpa.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MembersImgRepository extends JpaRepository<MembersImgEntity, Long> {
    MembersImgEntity findByMembersInfo(MembersInfoEntity membersInfoEntity);
}
