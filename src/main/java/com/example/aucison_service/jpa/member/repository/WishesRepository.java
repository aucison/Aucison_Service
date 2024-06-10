package com.example.aucison_service.jpa.member.repository;

import com.example.aucison_service.dto.wish.ProductWishCountDto;
import com.example.aucison_service.jpa.member.entity.MembersEntity;
import com.example.aucison_service.jpa.member.entity.WishesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishesRepository extends JpaRepository<WishesEntity, Long> {
    List<WishesEntity> findByMembersEntity(MembersEntity membersEntity);

    Optional<WishesEntity> findByMembersEntityAndProductId(MembersEntity member, Long productId);
    boolean existsByMembersEntityAndProductId(MembersEntity member, Long productId);


    //별도의 통계 쿼리 사용 -> 사용할 필요 없어짐 -> 모든 상품에 대한 찜 집계 쿼리였음
//    @Query("SELECT new com.example.aucison_service.dto.wish.ProductWishCountDto(w.productId, COUNT(w)) " +
//            "FROM WishesEntity w GROUP BY w.productId")
//    List<ProductWishCountDto> countWishesByProduct();

    //특정 상품에 대한 찜 집계
    Long countByProductId(Long productId);

    // 개수 계산을 위한 쿼리 메서드 추가
    @Query("SELECT COUNT(w) FROM WishesEntity w WHERE w.membersEntity = :member")
    int countByMember(@Param("member") MembersEntity member);

    void deleteByProductId(Long productsId);
}
