package com.example.aucison_service.jpa.member;

import com.amazonaws.services.ec2.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressesRepository extends JpaRepository<Addresses, Long> {
    Addresses findByMembersInfoAndAddr_name(MembersInfo membersInfo, String addrName);
}
