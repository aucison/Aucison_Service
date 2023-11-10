package com.example.aucison_service.jpa.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressesRepository extends JpaRepository<AddressesEntity, Long> {
    AddressesEntity findByMembersInfoAndAddr_name(MembersInfoEntity membersInfoEntity, String addrName);
}
