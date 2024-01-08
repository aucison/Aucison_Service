package com.example.aucison_service.jpa.member.repository;

import com.example.aucison_service.enums.OrderType;
import com.example.aucison_service.jpa.member.entity.HistoriesEntity;
import com.example.aucison_service.jpa.member.entity.MembersInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriesRepository extends JpaRepository<HistoriesEntity, Long> {
    List<HistoriesEntity> findByMembersInfoEntity(MembersInfoEntity membersInfoEntity);
    HistoriesEntity findByOrdersId(Long ordersId);
    List<HistoriesEntity> findByMembersInfoEntity_MembersEntity_EmailAndAndOrderType(String email, OrderType orderType);
}
