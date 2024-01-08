package com.example.aucison_service.jpa.shipping.repository;

import com.example.aucison_service.jpa.shipping.entity.Refunds;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundsRepository extends JpaRepository<Refunds, Long> {
}