package com.example.aucison_service.controller;


import com.example.aucison_service.dto.ApiResponse;
import com.example.aucison_service.dto.payments.PaymentsRequestDto;
import com.example.aucison_service.dto.payments.VirtualPaymentResponseDto;
import com.example.aucison_service.service.shipping.PaymentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class PaymentsController {

    private final PaymentsService paymentsService;

    @Autowired
    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }
    @GetMapping("/payment/{productsId}")    //가상결제
    public ApiResponse<VirtualPaymentResponseDto> getVirtualPaymentInfo(@PathVariable Long productsId,
                                                                        @RequestParam String email,
                                                                        @RequestParam String addrName,
                                                                        @RequestParam int percent) {
        return ApiResponse.createSuccess(paymentsService.getVirtualPaymentInfo(productsId, email, addrName, percent));
    }

    @PostMapping("/payment")    //결제완료
    public ApiResponse<Long> savePayment(@RequestBody PaymentsRequestDto paymentsRequestDto) {
        return ApiResponse.createSuccess(paymentsService.savePayment(paymentsRequestDto));
    }
}
