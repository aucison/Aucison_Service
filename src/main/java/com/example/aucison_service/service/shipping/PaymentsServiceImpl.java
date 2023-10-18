package com.example.aucison_service.service.shipping;

import com.example.Aucison_Shipping_Service.OrderStatus;
import com.example.Aucison_Shipping_Service.PageType;
import com.example.Aucison_Shipping_Service.client.MemberServiceClient;
import com.example.Aucison_Shipping_Service.client.ProductServiceClient;
import com.example.Aucison_Shipping_Service.dto.client.AddrInfoResponseDto;
import com.example.Aucison_Shipping_Service.dto.client.UpdateCreditRequestDto;
import com.example.Aucison_Shipping_Service.dto.client.VirtualPaymentProductInfoResponseDto;
import com.example.Aucison_Shipping_Service.dto.payments.PaymentsRequestDto;
import com.example.Aucison_Shipping_Service.dto.payments.VirtualPaymentResponseDto;
import com.example.Aucison_Shipping_Service.exception.AppException;
import com.example.Aucison_Shipping_Service.exception.ErrorCode;
import com.example.Aucison_Shipping_Service.jpa.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentsServiceImpl implements PaymentsService {
    private final ProductServiceClient productServiceClient;
    private final MemberServiceClient memberServiceClient;
    private final BidsRepository bidsRepository;
    private PageAccessLogsRepository pageAccessLogsRepository;
    private OrdersRepository ordersRepository;
    private PaymentsRepository paymentsRepository;
    private DeliveriesRepository deliveriesRepository;
    private RefundsRepository refundsRepository;

    @Autowired
    public PaymentsServiceImpl(ProductServiceClient productServiceClient, MemberServiceClient memberServiceClient,
                               BidsRepository bidsRepository, PageAccessLogsRepository pageAccessLogsRepository,
                               OrdersRepository ordersRepository, PaymentsRepository paymentsRepository,
                               DeliveriesRepository deliveriesRepository, RefundsRepository refundsRepository) {
        this.productServiceClient = productServiceClient;
        this.memberServiceClient = memberServiceClient;
        this.bidsRepository = bidsRepository;
        this.pageAccessLogsRepository = pageAccessLogsRepository;
        this.ordersRepository = ordersRepository;
        this.paymentsRepository = paymentsRepository;
        this.deliveriesRepository = deliveriesRepository;
        this.refundsRepository = refundsRepository;
    }

    @Override
    public VirtualPaymentResponseDto getVirtualPaymentInfo(Long productsId, String email,
                                                           String addrName, int percent) {  //가상 결제


        //MSA 통신을 사용하여 product-service에서 product 정보 가져오기
        VirtualPaymentProductInfoResponseDto product = productServiceClient.getVirtualPaymentProductByProductsId(productsId);

        if ("AUCS".equals(product.getCategory())) {
            //경매 상품
            return getAucsVirtualPaymentInfo(productsId, email, addrName, percent);
        } else if ("SALE".equals(product.getCategory())) {
            //비경매 상품
            return getSaleVirtualPaymentInfo(productsId, email, addrName);
        } else {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
    }

    public VirtualPaymentResponseDto getSaleVirtualPaymentInfo(Long productsId, String email,
                                                           String addrName) {  //가상 결제(비경매)

        //MSA 통신을 사용하여 product-service에서 product 정보 가져오기
        VirtualPaymentProductInfoResponseDto product = productServiceClient.getVirtualPaymentProductByProductsId(productsId);

        //MSA 통신을 사용하여 member-service에서 배송지 정보 가져오기
        AddrInfoResponseDto addrInfoResponseDto = fetchShippingInfo(email, addrName);

        // MSA 통신을 사용하여 member-service에서 credit 정보 가져오기
        float currentCredit = memberServiceClient.getCreditByEmail(email);

        //현재 credit에서 등록 가격을 차감
        float newCredit = currentCredit - product.getPrice();

        return VirtualPaymentResponseDto.builder()
                .category(product.getCategory())
                .name(product.getProductName())
                .productImg(product.getProductImg())
                .price(product.getPrice())
                .addrName(addrInfoResponseDto.getAddrName())
                .name(addrInfoResponseDto.getName())
                .tel(addrInfoResponseDto.getTel())
                .zipCode(addrInfoResponseDto.getZipCode())
                .addr(addrInfoResponseDto.getAddr())
                .addrDetail(addrInfoResponseDto.getAddrDetail())
                .credit(currentCredit)
                .newCredit(newCredit)
                .build();
    }

    public VirtualPaymentResponseDto getAucsVirtualPaymentInfo(Long productsId, String email,
                                                               String addrName, int percent) {  //가상 결제(경매)
        //가상 결제 페이지 접근 로그 생성 전에 체크
        LocalDateTime accessTime = LocalDateTime.now();
        if(!isBeforeAuctionEndDate(productsId, accessTime)) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }

        //가상 결제 페이지 접근 로그 생성
        Long logId = logPageAccess(productsId, email, PageType.VIRTUAL_PAYMENT);


        //MSA 통신을 사용하여 product-service에서 product 정보 가져오기
        VirtualPaymentProductInfoResponseDto product = productServiceClient.getVirtualPaymentProductByProductsId(productsId);

        //bids에서 실시간 가격 정보를 받아옴
        Bids bid = bidsRepository.findByBidsCode(product.getBidsCode());
        float nowPrice = bid.getNowPrice();

        //MSA 통신을 사용하여 member-service에서 배송지 정보 가져오기
        AddrInfoResponseDto addrInfoResponseDto = fetchShippingInfo(email, addrName);

        // MSA 통신을 사용하여 member-service에서 credit 정보 가져오기
        float currentCredit = memberServiceClient.getCreditByEmail(email);

        //현재 credit에서 경매 가격을 차감
        float newCredit = currentCredit - nowPrice;

        float newPrice = nowPrice + (nowPrice * (percent / 100));   //응찰가

        //가상 결제 페이지 탈출 로그 생성 전에 체크
        LocalDateTime exitTime = LocalDateTime.now();
        if(!isBeforeAuctionEndDate(productsId, exitTime)) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }

        logPageExit(logId);

        return VirtualPaymentResponseDto.builder()
                .category(product.getCategory())
                .name(product.getProductName())
                .productImg(product.getProductImg())
                .nowPrice(newPrice)
                .addrName(addrInfoResponseDto.getAddrName())
                .name(addrInfoResponseDto.getName())
                .tel(addrInfoResponseDto.getTel())
                .zipCode(addrInfoResponseDto.getZipCode())
                .addr(addrInfoResponseDto.getAddr())
                .addrDetail(addrInfoResponseDto.getAddrDetail())
                .credit(currentCredit)
                .newCredit(newCredit)
                .build();

    }

    private AddrInfoResponseDto fetchShippingInfo(String email, String addrName) {  //배송지명으로 배송지 조회
        AddrInfoResponseDto addrInfoResponseDto = memberServiceClient.getShippingInfo(email, addrName);
        if (addrInfoResponseDto == null) {
            throw new AppException(ErrorCode.SHIPPING_INFO_NOT_FOUND);
        }
        return addrInfoResponseDto;
    }

    @Override
    public Long savePayment(PaymentsRequestDto paymentsRequestDto) {    //결제완료
        if ("SALE".equals(paymentsRequestDto.getCategory())) {   //비경매
            return saveSalePayment(paymentsRequestDto);
        } else if ("AUCS".equals(paymentsRequestDto.getCategory())) {    //경매
            return saveAucsPayment(paymentsRequestDto);
        } else {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
    }

    public Long saveSalePayment(PaymentsRequestDto paymentsRequestDto) {    //결제완료(비경매)
        // Orders 정보 저장
        Orders order = Orders.builder()
                .productsId(paymentsRequestDto.getProductsId())
                .email(paymentsRequestDto.getEmail())
                .status(OrderStatus.ORDER_COMPLETED)
                .build();

        order = ordersRepository.save(order);

        // Payments 정보 저장
        Payments payment = Payments.builder()
                .cost(paymentsRequestDto.getPrice())
                .orders(order)
                .build();

        payment = paymentsRepository.save(payment);

        // Deliveries 정보 저장
        Deliveries delivery = Deliveries.builder()
                .addr(paymentsRequestDto.getAddr())
                .addrDetail(paymentsRequestDto.getAddrDetail())
                .addrName(paymentsRequestDto.getAddrName())
                .isCompleted(false)
                .isStarted(false)
                .name(paymentsRequestDto.getName())
                .tel(paymentsRequestDto.getTel())
                .orders(order)
                .build();

        delivery = deliveriesRepository.save(delivery);

        // MSA 통신을 사용하여 member-service에서 credit 정보 가져오기
        float currentCredit = memberServiceClient.getCreditByEmail(paymentsRequestDto.getEmail());
        float updatedCredit = currentCredit - paymentsRequestDto.getPrice();

        // MSA 통신을 사용하여 결제 금액을 차감한 credit 정보 업데이트
        UpdateCreditRequestDto updateCreditRequestDto = UpdateCreditRequestDto.builder()
                .email(paymentsRequestDto.getEmail())
                .credit(updatedCredit)
                .build();

        memberServiceClient.updateCreditByEmail(updateCreditRequestDto);

        return order.getOrdersId();
    }

    public Long saveAucsPayment(PaymentsRequestDto paymentsRequestDto) {    //결제완료(경매)
        //결제 페이지 접근 로그 생성 전에 체크
        //TODO: 3분 연장 로직, status 판단 로직
        LocalDateTime accessTime = LocalDateTime.now();
        if (!isBeforeAuctionEndDate(paymentsRequestDto.getProductsId(), accessTime)) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }

        //결제 페이지 접근 로그 생성
        Long logId = logPageAccess(paymentsRequestDto.getProductsId(), paymentsRequestDto.getEmail(),
                PageType.PAYMENT_COMPLETED);


        // Orders 정보 저장
        Orders order = Orders.builder()
                .productsId(paymentsRequestDto.getProductsId())
                .email(paymentsRequestDto.getEmail())
                .status(OrderStatus.WAITING_FOR_BID)
                .build();

        order = ordersRepository.save(order);

        // Payments 정보 저장
        Payments payment = Payments.builder()
                .cost(paymentsRequestDto.getNowPrice())
                .orders(order)
                .build();

        payment = paymentsRepository.save(payment);

        // Deliveries 정보 저장
        Deliveries delivery = Deliveries.builder()
                .addr(paymentsRequestDto.getAddr())
                .addrDetail(paymentsRequestDto.getAddrDetail())
                .addrName(paymentsRequestDto.getAddrName())
                .isCompleted(false)
                .isStarted(false)
                .name(paymentsRequestDto.getName())
                .tel(paymentsRequestDto.getTel())
                .orders(order)
                .build();

        delivery = deliveriesRepository.save(delivery);

        // Bids 정보 저장
        Bids bid = Bids.builder()
                .productsId(paymentsRequestDto.getProductsId())
                .email(paymentsRequestDto.getEmail())
                .nowPrice(paymentsRequestDto.getNowPrice())
                .status(OrderStatus.WAITING_FOR_BID)
                // Bids code는 고유하게 생성하는 로직 필요
                .bidsCode(UUID.randomUUID().toString())
                .build();

        bid = bidsRepository.save(bid);

        // MSA 통신을 사용하여 member-service에서 credit 정보 가져오기
        float currentCredit = memberServiceClient.getCreditByEmail(paymentsRequestDto.getEmail());
        float updatedCredit = currentCredit - paymentsRequestDto.getNowPrice();

        // MSA 통신을 사용하여 결제 금액을 차감한 credit 정보 업데이트
        UpdateCreditRequestDto updateCreditRequestDto = UpdateCreditRequestDto.builder()
                .email(paymentsRequestDto.getEmail())
                .credit(updatedCredit)
                .build();

        memberServiceClient.updateCreditByEmail(updateCreditRequestDto);

        //경매 미낙찰에 따른 환불
        //상품 id로 해당 상품 주문 정보를 모두 찾음
        List<Orders> existingOrders = ordersRepository.findAllByProductsId(paymentsRequestDto.getProductsId());

        Orders winningOrder = existingOrders.get(0);

        for (Orders ord : existingOrders) {
            //새로운 주문이 아니고 "응찰" 상태였던 이전 주문이라면
            if (!ord.equals(winningOrder) && ord.getStatus().equals(OrderStatus.WAITING_FOR_BID)) {

                ord.updateStatus(OrderStatus.FAILED_BID);   //이전 응찰은 "패찰"로 변해야 함

                float refundedAmount = ord.getPayments().getCost();   //환불해 줄 금액

                // MSA 통신을 사용하여 member-service에서 credit 정보 가져오기
                currentCredit = memberServiceClient.getCreditByEmail(ord.getEmail());
                updatedCredit = currentCredit + refundedAmount; // 현재 credit에서 환불해 줄 금액을 더한 뒤 credit에 반영

                // `member-service`에 credit 업데이트 요청
                updateCreditRequestDto = UpdateCreditRequestDto.builder()
                        .email(ord.getEmail())
                        .credit(updatedCredit)
                        .build();

                memberServiceClient.updateCreditByEmail(updateCreditRequestDto);

                // 환불 정보 저장
                Refunds refund = Refunds.builder()
                        .cost(refundedAmount)
                        .build();
                refundsRepository.save(refund);

                // 실시간 응찰 내역에 패찰 정보 저장
                Bids failedBid = Bids.builder()
                        .productsId(paymentsRequestDto.getProductsId())
                        .email(ord.getEmail())
                        .nowPrice(ord.getPayments().getCost())
//                        .bidsAt(new Date())
                        .status(OrderStatus.FAILED_BID)
                        .bidsCode(UUID.randomUUID().toString())
                        .build();
                bidsRepository.save(failedBid);

            }
        }

        //가상 결제 페이지 탈출 로그 생성 전에 체크
        LocalDateTime exitTime = LocalDateTime.now();
        if(!isBeforeAuctionEndDate(paymentsRequestDto.getProductsId(), exitTime)) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }

        logPageExit(logId);

        return order.getOrdersId();
    }

    // 페이지에 접근했을 때의 로그 생성
    public Long logPageAccess(Long productId, String email, PageType pageType) {
        PageAccessLogs log = PageAccessLogs.builder()
                .productsId(productId)
                .email(email)
                .pageType(pageType)
                .build();

        log = pageAccessLogsRepository.save(log);
        return log.getPageAccessLogsId();
    }

    // 페이지에서 나갔을 때의 로그 갱신
    public void logPageExit(Long logId) {
        // logId는 페이지 접근 시 저장된 로그의 ID
        PageAccessLogs existingLog = pageAccessLogsRepository.findById(logId)
                .orElseThrow(() -> new AppException(ErrorCode.LOG_NOT_FOUND));
        // BaseTimeEntity의 modifiedDate는 자동으로 갱신
        // JpaRepository의 save 메서드를 호출하여 로그를 갱신
        pageAccessLogsRepository.save(existingLog);
    }

    private boolean isBeforeAuctionEndDate(Long productsId, LocalDateTime dateTimeToCheck) {
        //종료 날짜를 받아와 현재 시간과 비교하여 true 또는 false를 반환
        LocalDateTime auctionEndDate = productServiceClient.getAuctionEndDateByProductId(productsId);
        return dateTimeToCheck.isBefore(auctionEndDate);
    }
}
