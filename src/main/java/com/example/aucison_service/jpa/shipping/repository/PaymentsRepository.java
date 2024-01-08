package com.example.aucison_service.jpa.shipping.repository;

import com.example.aucison_service.jpa.shipping.entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentsRepository extends JpaRepository<Payments, Long> {
}
