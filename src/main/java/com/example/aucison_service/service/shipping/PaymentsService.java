package com.example.aucison_service.service.shipping;


import com.example.aucison_service.dto.payments.PaymentsRequestDto;
import com.example.aucison_service.dto.payments.VirtualPaymentResponseDto;
import com.example.aucison_service.service.member.MemberDetails;

import java.util.Optional;

public interface PaymentsService {
    /**
     * 경매상품 가상 결제 조회
     *
     * @param productsId 상품 ID
     * @param bidAmount 입찰희망가
     * @return PaymentPageResponse
     */
    VirtualPaymentResponseDto getVirtualPaymentInfo(Long productsId, MemberDetails principal, Optional<Float> bidAmount);

//    /**
//     * 배송지 새롭게 조회
//     *
//     * @param productsId 상품 ID
//     * @param email 사용자 이메일
//     * @param addrName 배송지명
//     * @return AddrInfoResponseDto
//     */
//    AddrInfoResponseDto getShippingInfo(Long productsId, String email, String addrName);

    /**
     * 주어진 정보를 기반으로 결제 정보를 저장합니다.
     * @param paymentsRequestDto 결제와 관련된 정보를 담고 있는 DTO
     * @return ordersId(주문번호)
     */
    Long savePayment(MemberDetails principal, PaymentsRequestDto paymentsRequestDto);

    void saveAucsPaymentInfo(String email, PaymentsRequestDto paymentsRequestDto);
}