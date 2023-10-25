package com.example.aucison_service.jpa.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MembersImgRepository extends JpaRepository<MembersImg, Long> {
    MembersImg findByMembersInfo(MembersInfo membersInfo);
}
