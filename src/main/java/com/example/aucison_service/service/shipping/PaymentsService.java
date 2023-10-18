package com.example.aucison_service.service.shipping;

import com.example.Aucison_Shipping_Service.dto.payments.PaymentsRequestDto;
import com.example.Aucison_Shipping_Service.dto.payments.VirtualPaymentResponseDto;

public interface PaymentsService {
    /**
     * 가상 결제 조회
     *
     * @param productsId 상품 ID
     * @param email 사용자 이메일
     * @param addrName 배송지명
     * @param percent 구매자가 올린 퍼센트
     * @return PaymentPageResponse
     */
    VirtualPaymentResponseDto getVirtualPaymentInfo(Long productsId, String email, String addrName, int percent);

    /**
     * 주어진 정보를 기반으로 결제 정보를 저장합니다.
     * @param paymentsRequestDto 결제와 관련된 정보를 담고 있는 DTO
     * @return ordersId(주문번호)
     */
    Long savePayment(PaymentsRequestDto paymentsRequestDto);


}
