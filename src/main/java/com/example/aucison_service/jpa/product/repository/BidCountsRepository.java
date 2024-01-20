package com.example.aucison_service.jpa.product.repository;

import com.example.aucison_service.jpa.product.entity.BidCountsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidCountsRepository extends JpaRepository<BidCountsEntity, Long> {
   BidCountsEntity findByProductsId(Long productsId);
}
