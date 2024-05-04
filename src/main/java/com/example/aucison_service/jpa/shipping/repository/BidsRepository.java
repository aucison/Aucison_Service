package com.example.aucison_service.jpa.shipping.repository;

import com.example.aucison_service.jpa.shipping.entity.Bids;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidsRepository extends JpaRepository<Bids, Long> {
    Bids findByBidsCode(String bidsCode);
    List<Bids> findByProductsIdAndAndEmail(Long productsId, String email);

    void deleteByProductsId(Long productsId);
}
