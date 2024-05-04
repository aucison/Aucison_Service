package com.example.aucison_service.jpa.member.repository;

import com.example.aucison_service.jpa.member.entity.MembersEntity;
import com.example.aucison_service.jpa.member.entity.MembersInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembersInfoRepository extends JpaRepository<MembersInfoEntity, Long> {
    MembersInfoEntity findByMembersEntity(MembersEntity membersEntity);



}
