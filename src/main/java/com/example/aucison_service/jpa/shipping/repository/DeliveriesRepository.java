package com.example.aucison_service.jpa.shipping.repository;

import com.example.aucison_service.jpa.shipping.entity.Deliveries;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveriesRepository extends JpaRepository<Deliveries, Long> {
    Deliveries findByOrdersOrdersId(Long ordersId);
}
