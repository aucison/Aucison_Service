package com.example.aucison_service.jpa.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressesRepository extends JpaRepository<AddressesEntity, Long> {
    AddressesEntity findByMembersInfoEntityAndAddrName(MembersInfoEntity membersInfoEntity, String addrName);
    List<AddressesEntity> findAllByMembersInfoEntity(MembersInfoEntity membersInfoEntity);
    boolean existsByAddrNameAndMembersInfoEntity(String addrName, MembersInfoEntity membersInfo);
}
