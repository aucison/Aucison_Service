package com.example.aucison_service.jpa.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriesRepository extends JpaRepository<Histories, Long> {
    List<Histories> findByMembersInfo(MembersInfo membersInfo);
}
