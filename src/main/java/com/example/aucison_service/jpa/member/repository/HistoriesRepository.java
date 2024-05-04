package com.example.aucison_service.jpa.member.repository;

import com.example.aucison_service.enums.OrderType;
import com.example.aucison_service.jpa.member.entity.HistoriesEntity;
import com.example.aucison_service.jpa.member.entity.MembersInfoEntity;
import com.example.aucison_service.jpa.product.entity.ProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriesRepository extends JpaRepository<HistoriesEntity, Long> {
    List<HistoriesEntity> findByMembersInfoEntityAndOrderType(Long membersInfoId, OrderType orderType);
    HistoriesEntity findByHistoriesId(Long historiesId);
    HistoriesEntity findByOrdersId(Long ordersId);

    HistoriesEntity findByProductsIdAndEmail(Long productsId, String email);

    //특정 상품(판매명으로 식별)의 판매자(상태 분류로 식별) 반환
    HistoriesEntity findByNameAndOrderType(String name, OrderType orderType);

    // 개수 계산을 위한 쿼리 메서드 추가
    @Query("SELECT COUNT(h) FROM HistoriesEntity h WHERE h.membersInfoEntity.id = :membersInfoId AND h.orderType = :orderType")
    int countByOrderTypeAndMembersInfoId(@Param("membersInfoId") Long membersInfoId, @Param("orderType") OrderType orderType);

}
