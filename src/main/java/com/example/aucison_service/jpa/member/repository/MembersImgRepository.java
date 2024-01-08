package com.example.aucison_service.jpa.member.repository;

import com.example.aucison_service.jpa.member.entity.MembersImgEntity;
import com.example.aucison_service.jpa.member.entity.MembersInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembersImgRepository extends JpaRepository<MembersImgEntity, Long> {
    MembersImgEntity findByMembersInfoEntity(MembersInfoEntity membersInfoEntity);
}
