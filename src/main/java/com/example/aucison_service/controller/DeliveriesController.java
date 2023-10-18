package com.example.aucison_service.controller;

import com.example.Aucison_Shipping_Service.dto.ApiResponse;
import com.example.Aucison_Shipping_Service.dto.deliveries.DeliveriesResponseDto;
import com.example.Aucison_Shipping_Service.service.DeliveriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shipping-service")
public class DeliveriesController {
    private final DeliveriesService deliveriesService;
    @Autowired
    public DeliveriesController(DeliveriesService deliveriesService) {
        this.deliveriesService = deliveriesService;
    }

    @GetMapping("/{ordersId}")
    public ApiResponse<DeliveriesResponseDto> getDeliveryByOrdersId(@PathVariable Long ordersId) {  //orders_id로 배송지 조회
        return ApiResponse.createSuccess(deliveriesService.getDeliveryByOrdersId(ordersId));
    }
}
