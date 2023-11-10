package com.example.aucison_service.jpa.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressesRepository extends JpaRepository<AddressesEntity, Long> {
    AddressesEntity findByMembersInfoEntityAndAddrName(MembersInfoEntity membersInfoEntity, String addrName);
}
