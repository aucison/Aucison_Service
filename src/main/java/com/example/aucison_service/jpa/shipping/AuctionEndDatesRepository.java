package com.example.aucison_service.jpa.shipping;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionEndDatesRepository extends JpaRepository<AuctionEndDatesEntity, Long> {
    AuctionEndDatesEntity findByProductsId(Long productsId);
}
