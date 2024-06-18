package com.example.aucison_service.controller;


import com.example.aucison_service.dto.ApiResponse;
import com.example.aucison_service.dto.payments.PaymentsRequestDto;
import com.example.aucison_service.dto.payments.VirtualPaymentResponseDto;
import com.example.aucison_service.service.member.MemberDetails;
import com.example.aucison_service.service.shipping.PaymentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/payment")
public class PaymentsController {

    private final PaymentsService paymentsService;

    @Autowired
    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    @GetMapping("/{productsId}")    //가상결제
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<VirtualPaymentResponseDto> getVirtualPaymentInfo(@PathVariable Long productsId,
                                                                        @AuthenticationPrincipal MemberDetails principal,
                                                                        @RequestParam Optional<Float> bidAmount) {
        return ApiResponse.createSuccess(paymentsService.getVirtualPaymentInfo(productsId, principal, bidAmount));
    }


//    @GetMapping("/{productsId}/shipping-address")   //배송지 새롭게 조회
//    public ApiResponse<?> getShippingAddress(@PathVariable Long productsId,
//                                             @RequestParam String email,
//                                             @RequestParam String addrName) {
//        // 서비스 로직을 호출하여 배송지 정보 조회
//        return ApiResponse.createSuccess(paymentsService.getShippingInfo(productsId, email, addrName));
//    }


    @PostMapping   //결제완료
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Long> savePayment(@AuthenticationPrincipal MemberDetails principal,
                                         @RequestBody PaymentsRequestDto paymentsRequestDto) {
        return ApiResponse.createSuccess(paymentsService.savePayment(principal, paymentsRequestDto));
    }
}