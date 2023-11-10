package com.example.aucison_service.jpa.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriesRepository extends JpaRepository<HistoriesEntity, Long> {
    List<HistoriesEntity> findByMembersInfoEntity(MembersInfoEntity membersInfoEntity);
    HistoriesEntity findByOrdersId(Long ordersId);
}
