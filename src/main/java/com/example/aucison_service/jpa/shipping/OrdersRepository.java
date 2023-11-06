package com.example.aucison_service.jpa.shipping;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
//    List<Orders> findAllByEmail(String email);
    List<Orders> findAllByProductsId(Long productsId);
    Orders findByOrdersId(Long ordersId);   //주문번호(orders_id)로 주문내역 조회
}
