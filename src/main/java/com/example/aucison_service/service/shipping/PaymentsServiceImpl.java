package com.example.aucison_service.service.shipping;


import com.example.aucison_service.dto.payments.AddrInfoResponseDto;
import com.example.aucison_service.dto.payments.PaymentsRequestDto;
import com.example.aucison_service.dto.payments.VirtualPaymentResponseDto;
import com.example.aucison_service.enums.OrderStatus;
import com.example.aucison_service.enums.PageType;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.member.*;
import com.example.aucison_service.jpa.product.*;
import com.example.aucison_service.jpa.shipping.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentsServiceImpl implements PaymentsService {
    private final BidsRepository bidsRepository;
    private final PageAccessLogsRepository pageAccessLogsRepository;
    private final OrdersRepository ordersRepository;
    private final PaymentsRepository paymentsRepository;
    private final DeliveriesRepository deliveriesRepository;
    private final RefundsRepository refundsRepository;
    private final ProductsRepository productsRepository;
    private final MembersRepository membersRepository;
    private final MembersInfoRepository membersInfoRepository;
    private final AddressesRepository addressesRepository;
    private final SaleInfosRepository saleInfosRepository;
    private final AucsInfosRepository aucsInfosRepository;
//    private final ProductImgRepository productImgRepository;

    @Autowired
    public PaymentsServiceImpl(BidsRepository bidsRepository, PageAccessLogsRepository pageAccessLogsRepository,
                               OrdersRepository ordersRepository, PaymentsRepository paymentsRepository,
                               DeliveriesRepository deliveriesRepository, RefundsRepository refundsRepository,
                               ProductsRepository productsRepository, MembersRepository membersRepository,
                               MembersInfoRepository membersInfoRepository, AddressesRepository addressesRepository,
                               SaleInfosRepository saleInfosRepository, AucsInfosRepository aucsInfosRepository
                               /*, ProductImgRepository productImgRepository*/) {
        this.bidsRepository = bidsRepository;
        this.pageAccessLogsRepository = pageAccessLogsRepository;
        this.ordersRepository = ordersRepository;
        this.paymentsRepository = paymentsRepository;
        this.deliveriesRepository = deliveriesRepository;
        this.refundsRepository = refundsRepository;
        this.productsRepository = productsRepository;
        this.membersRepository = membersRepository;
        this.membersInfoRepository = membersInfoRepository;
        this.addressesRepository = addressesRepository;
        this.saleInfosRepository = saleInfosRepository;
        this.aucsInfosRepository = aucsInfosRepository;
//        this.productImgRepository = productImgRepository;
    }

    @Override
    public VirtualPaymentResponseDto getVirtualPaymentInfo(Long productsId, String email,
                                                           String addrName, int percent) {  //가상 결제

        //product 정보 가져오기
        ProductsEntity product = productsRepository.findByProductsId(productsId);

        if ("AUCS".equals(product.getCategory())) {
            //경매 상품 가상 결제
            return getAucsVirtualPaymentInfo(productsId, email, addrName, percent);
        } else if ("SALE".equals(product.getCategory())) {
            //판매(비경매) 상품 가상 결제
            return getSaleVirtualPaymentInfo(productsId, email, addrName);
        } else {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }

    }

    public VirtualPaymentResponseDto getSaleVirtualPaymentInfo(Long productsId, String email,
                                                               String addrName) {  //가상 결제(비경매)

        //product 정보 가져오기
        ProductsEntity product = productsRepository.findByProductsId(productsId);

        //AddrInfoResponseDto addrInfoResponseDto = fetchShippingInfo(email, addrName);
        MembersEntity membersEntity = membersRepository.findByEmail(email);
        MembersInfo membersInfo = membersInfoRepository.findByMembersEntity(membersEntity);

        //배송지 정보 가져오기
        AddrInfoResponseDto addresses = getShippingInfo(productsId, email, addrName);

        //credit 정보 가져오기
        float currentCredit = membersInfo.getCredit();

        //등록 가격(판매) 가져오기
        SaleInfosEntity saleInfosEntity = saleInfosRepository.findByProductsEntity(product);

        //현재 credit에서 등록 가격을 차감
        float newCredit = currentCredit - saleInfosEntity.getPrice();

        if (newCredit < 0) {    //사용자의 credit이 결제하려는 금액보다 적을 경우
            throw new AppException(ErrorCode.INSUFFICIENT_CREDIT);
        }

        //product 이미지 중 대표(첫 번째 url 반환)
        List<ProductImgEntity> images = productImgRepository.findByProductProductsIdOrderByProductImgIdAsc(productsId);
        String image = null;
        if (!images.isEmpty()) {
            image =  images.get(0).getUrl(); // 첫 번째 이미지 URL 반환
        } else {
            image = null; // 이미지가 없으면 null 반환
        }

        return VirtualPaymentResponseDto.builder()
                .category(product.getCategory())
                .name(product.getName())
                .productImg(image)
                .price(saleInfosEntity.getPrice())
                .addrName(addresses.getAddrName())
                .name(addresses.getName())
                .tel(addresses.getTel())
                .zipCode(addresses.getZipCode())
                .addr(addresses.getAddr())
                .addrDetail(addresses.getAddrDetail())
                .credit(currentCredit)
                .newCredit(newCredit)
                .build();
    }

    public VirtualPaymentResponseDto getAucsVirtualPaymentInfo(Long productsId, String email,
                                                               String addrName, int percent) {  //가상 결제(경매)
        //가상 결제 페이지 접근 로그 생성 전에 체크
        //TODO: 현재 시간과 어떻게 비교할지 좀 더 고려
        Date accessTime = new Date();
        if(!isBeforeAuctionEndDate(productsId, accessTime)) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }

        //가상 결제 페이지 접근 로그 생성
        Long logId = logPageAccess(productsId, email, PageType.VIRTUAL_PAYMENT);

        //product 정보 가져오기
        //VirtualPaymentProductInfoResponseDto product = productServiceClient.getVirtualPaymentProductByProductsId(productsId);
        ProductsEntity product = productsRepository.findByProductsId(productsId);

        //bids에서 실시간 가격 정보를 받아옴
        AucsInfosEntity aucsInfo = aucsInfosRepository.findByProductsEntity(product);
        Bids bid = bidsRepository.findByBidsCode(aucsInfo.getBidsCode());
        float nowPrice = bid.getNowPrice();

        //AddrInfoResponseDto addrInfoResponseDto = fetchShippingInfo(email, addrName);
        MembersEntity membersEntity = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        MembersInfo membersInfo = membersInfoRepository.findByMembersEntity(membersEntity);

        //배송지 정보 가져오기
        AddrInfoResponseDto addresses = getShippingInfo(productsId, email, addrName);

        //credit 정보 가져오기
        float currentCredit = membersInfo.getCredit();

        //현재 credit에서 경매 가격을 차감
        float newCredit = currentCredit - nowPrice;

        float newPrice = nowPrice + (nowPrice * (percent / 100));   //응찰가

        //가상 결제 페이지 탈출 로그 생성 전에 체크
        Date exitTime = new Date();
        if(!isBeforeAuctionEndDate(productsId, exitTime)) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }

        logPageExit(logId);

        //product 이미지 중 대표(첫 번째 url 반환)
        List<ProductImgEntity> images = productImgRepository.findByProductProductsIdOrderByProductImgIdAsc(productsId);
        String image = null;
        if (!images.isEmpty()) {
            image =  images.get(0).getUrl(); // 첫 번째 이미지 URL 반환
        } else {
            image = null; // 이미지가 없으면 null 반환
        }

        return VirtualPaymentResponseDto.builder()
                .category(product.getCategory())
                .name(product.getName())
                .productImg(image)
                .nowPrice(newPrice)
                .addrName(addresses.getAddrName())
                .name(addresses.getName())
                .tel(addresses.getTel())
                .zipCode(addresses.getZipCode())
                .addr(addresses.getAddr())
                .addrDetail(addresses.getAddrDetail())
                .credit(currentCredit)
                .newCredit(newCredit)
                .build();

    }

    @Override
    public AddrInfoResponseDto getShippingInfo(Long productsId, String email, String addrName) {  //배송지명으로 배송지 조회
        //AddrInfoResponseDto addrInfoResponseDto = memberServiceClient.getShippingInfo(email, addrName);
        MembersEntity membersEntity = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
        MembersInfo membersInfo = membersInfoRepository.findByMembersEntity(membersEntity);
        Addresses addresses = addressesRepository.findByMembersInfoAndAddr_name(membersInfo, addrName);

        if (addresses == null) {
            throw new AppException(ErrorCode.SHIPPING_INFO_NOT_FOUND);
        }
        return AddrInfoResponseDto.builder()
                .addrName(addresses.getAddr_name())
                .name(addresses.getName())
                .tel(addresses.getTel())
                .zipCode(addresses.getZip_num())
                .addr(addresses.getAddr())
                .addrDetail(addresses.getAddr_detail())
                .build();
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

        // credit 정보 가져오기
        MembersEntity membersEntity = membersRepository.findByEmail(paymentsRequestDto.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
        MembersInfo membersInfo = membersInfoRepository.findByMembersEntity(membersEntity);

        float currentCredit = membersInfo.getCredit();
        float updatedCredit = currentCredit - paymentsRequestDto.getPrice();

        // 결제 금액을 차감한 credit 정보 업데이트
//        UpdateCreditRequestDto updateCreditRequestDto = UpdateCreditRequestDto.builder()
//                .email(paymentsRequestDto.getEmail())
//                .credit(updatedCredit)
//                .build();
        membersInfo.updateCredit(updatedCredit);

        return order.getOrdersId();
    }

    public Long saveAucsPayment(PaymentsRequestDto paymentsRequestDto) {    //결제완료(경매)
        //결제 페이지 접근 로그 생성 전에 체크
        //TODO: 3분 연장 로직, status 판단 로직
        Date accessTime = new Date();
        if (!isBeforeAuctionEndDate(paymentsRequestDto.getProductsId(), accessTime)) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }

        //결제 페이지 접근 로그 생성
        Long logId = logPageAccess(paymentsRequestDto.getProductsId(), paymentsRequestDto.getEmail(),
                PageType.PAYMENT_COMPLETED);

        PageAccessLogs accessLog = pageAccessLogsRepository.findById(logId)
                .orElseThrow(() -> new AppException(ErrorCode.LOG_NOT_FOUND)); // 로그를 찾지 못한 경우 예외 발생


        //3분 연장 판단
        ProductsEntity product = productsRepository.findByProductsId(paymentsRequestDto.getProductsId());
        AucsInfosEntity aucsInfo = aucsInfosRepository.findByProductsEntity(product);

        Date auctionAccessTime = Date.from(accessLog.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant()); // 로그의 생성 시간(Date로 변경해줌)
        Date auctionEndTime = aucsInfo.getEnd(); // 경매의 종료 시간

        long timeDifference = auctionEndTime.getTime() - auctionAccessTime.getTime(); // 시간 차이를 밀리초 단위로 계산

        Long orderId = null;
        if (timeDifference >= 3 * 60 * 1000 && timeDifference <= 10 * 60 * 1000) {
            aucsInfo.extendAuctionEndTimeByMinutes(3); // 경매 종료 시간을 3분 연장하는 메소드 호출
            aucsInfosRepository.save(aucsInfo);
            orderId = saveExtendedAuctionOrder(paymentsRequestDto, aucsInfo);
        } else if (timeDifference < 3 * 60 * 1000) {
            orderId = saveFinalizedAuctionOrder(paymentsRequestDto);
        }

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

        //         TODO: msa 통신 부분 대체 필요
        // credit 정보 가져오기
        MembersEntity membersEntity = membersRepository.findByEmail(paymentsRequestDto.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
        MembersInfo membersInfo = membersInfoRepository.findByMembersEntity(membersEntity);

        float currentCredit = membersInfo.getCredit();
        float updatedCredit = currentCredit - paymentsRequestDto.getPrice();

        //결제 금액을 차감한 credit 정보 업데이트
//        UpdateCreditRequestDto updateCreditRequestDto = UpdateCreditRequestDto.builder()
//                .email(paymentsRequestDto.getEmail())
//                .credit(updatedCredit)
//                .build();
        membersInfo.updateCredit(updatedCredit);

        //경매 미낙찰에 따른 환불
        //상품 id로 해당 상품 주문 정보를 모두 찾음
        List<Orders> existingOrders = ordersRepository.findAllByProductsId(paymentsRequestDto.getProductsId());

        Orders winningOrder = existingOrders.get(0);

        for (Orders ord : existingOrders) {
            //새로운 주문이 아니고 "응찰" 상태였던 이전 주문이라면
            if (!ord.equals(winningOrder) && ord.getStatus().equals(OrderStatus.WAITING_FOR_BID)) {

                ord.updateStatus(OrderStatus.FAILED_BID);   //이전 응찰은 "패찰"로 변해야 함

                float refundedAmount = ord.getPayments().getCost();   //환불해 줄 금액

                //credit 정보 가져오기
                currentCredit = membersInfo.getCredit();
                updatedCredit = currentCredit + refundedAmount; // 현재 credit에서 환불해 줄 금액을 더한 뒤 credit에 반영

                // credit 업데이트 요청
                membersInfo.updateCredit(updatedCredit);

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
        Date exitTime = new Date();
        if(!isBeforeAuctionEndDate(paymentsRequestDto.getProductsId(), exitTime)) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }

        logPageExit(logId);

        return orderId;
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

    // 경매 연장에 따른 Orders 정보 저장 메서드
    private Long saveExtendedAuctionOrder(PaymentsRequestDto paymentsRequestDto, AucsInfosEntity aucsInfo) {
        Orders order = Orders.builder()
                .productsId(paymentsRequestDto.getProductsId())
                .email(paymentsRequestDto.getEmail())
                .status(OrderStatus.WAITING_FOR_BID)
                .build();
        order = ordersRepository.save(order);
        savePaymentAndDelivery(paymentsRequestDto, order);
        return order.getOrdersId();
    }

    // 경매 종료에 따른 Orders 정보 저장 메서드
    private Long saveFinalizedAuctionOrder(PaymentsRequestDto paymentsRequestDto) {
        Orders order = Orders.builder()
                .productsId(paymentsRequestDto.getProductsId())
                .email(paymentsRequestDto.getEmail())
                .status(OrderStatus.WINNING_BID)
                .build();
        order = ordersRepository.save(order);
        savePaymentAndDelivery(paymentsRequestDto, order);
        return order.getOrdersId();
    }

    // Payment와 Delivery 저장을 위한 공통 메서드
    private void savePaymentAndDelivery(PaymentsRequestDto paymentsRequestDto, Orders order) {
        Payments payment = Payments.builder()
                .cost(paymentsRequestDto.getNowPrice())
                .orders(order)
                .build();
        paymentsRepository.save(payment);

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
        deliveriesRepository.save(delivery);
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

    private boolean isBeforeAuctionEndDate(Long productsId, Date dateTimeToCheck) {
        //종료 날짜를 받아와 현재 시간과 비교하여 true 또는 false를 반환
        //         TODO: 시간 비교 로직 구현 시 Date를 사용해서
        //LocalDateTime auctionEndDate = productServiceClient.getAuctionEndDateByProductId(productsId);
        ProductsEntity product = productsRepository.findByProductsId(productsId);
        AucsInfosEntity aucsInfo = aucsInfosRepository.findByProductsEntity(product);

        Date auctionEndDate = aucsInfo.getEnd();

        return dateTimeToCheck.before(auctionEndDate);
    }
}