package com.example.aucison_service.jpa.shipping;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveriesRepository extends JpaRepository<Deliveries, Long> {
    Deliveries findByOrdersOrdersId(Long ordersId);
}
