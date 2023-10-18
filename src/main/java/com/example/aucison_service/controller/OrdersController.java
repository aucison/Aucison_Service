package com.example.aucison_service.controller;

import com.example.Aucison_Shipping_Service.dto.ApiResponse;
import com.example.Aucison_Shipping_Service.dto.orders.OrdersResponseDto;
import com.example.Aucison_Shipping_Service.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shipping-service")
public class OrdersController {
    private final OrdersService ordersService;

    @Autowired
    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @GetMapping("/orders/by-email/{email}") //이메일로 주문정보 전체 조회
    public ApiResponse<List<OrdersResponseDto>> findAllOrdersByEmail(@PathVariable String email) {
        return ApiResponse.createSuccess(ordersService.findAllOrdersByEmail(email));
    }

    @GetMapping("/orders/by-id/{ordersId}") //주문번호로 특정 주문정보만 조회
    public ApiResponse<OrdersResponseDto> findOrderByOrdersId(@PathVariable Long ordersId) {
        return ApiResponse.createSuccess(ordersService.findOrderByOrdersId(ordersId));
    }
}
