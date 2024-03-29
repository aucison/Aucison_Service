package com.example.aucison_service.jpa.shipping.repository;

import com.example.aucison_service.jpa.shipping.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrdersRepository extends JpaRepository<Orders, Long> {
//    List<Orders> findAllByEmail(String email);
    List<Orders> findAllByProductsId(Long productsId);
    Orders findByOrdersId(Long ordersId);
}
