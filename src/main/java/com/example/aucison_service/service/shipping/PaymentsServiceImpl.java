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
import com.example.aucison_service.service.member.MemberDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
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
   private final ProductImgRepository productImgRepository;

    @Autowired
    public PaymentsServiceImpl(BidsRepository bidsRepository, PageAccessLogsRepository pageAccessLogsRepository,
                               OrdersRepository ordersRepository, PaymentsRepository paymentsRepository,
                               DeliveriesRepository deliveriesRepository, RefundsRepository refundsRepository,
                               ProductsRepository productsRepository, MembersRepository membersRepository,
                               MembersInfoRepository membersInfoRepository, AddressesRepository addressesRepository,
                               SaleInfosRepository saleInfosRepository, AucsInfosRepository aucsInfosRepository
                               , ProductImgRepository productImgRepository) {
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
        this.productImgRepository = productImgRepository;
    }

    @Override
    @Transactional
    public VirtualPaymentResponseDto getAucsVirtualPaymentInfo(Long productsId, MemberDetails principal,
                                                           String addrName, int percent) {  //가상 결제(경매)
        String email = principal.getMember().getEmail();

        //product 정보 가져오기
        ProductsEntity product = productsRepository.findByProductsId(productsId);

        if ("AUCS".equals(product.getCategory())) {
            //경매 상품 가상 결제
            return getAucsVirtualPaymentInfo(productsId, email, addrName, percent);
        }  else {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }

    }

    @Override
    @Transactional
    public VirtualPaymentResponseDto getSaleVirtualPaymentInfo(Long productsId, MemberDetails principal,
                                                               String addrName) {  //가상 결제(비경매)
        String email = principal.getMember().getEmail();

        //product 정보 가져오기
        ProductsEntity product = productsRepository.findByProductsId(productsId);

        if ("SALE".equals(product.getCategory())) {
            //경매 상품 가상 결제
            return getSaleVirtualPaymentInfo(productsId, email, addrName);
        }  else {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }

    }

    @Transactional
    public VirtualPaymentResponseDto getSaleVirtualPaymentInfo(Long productsId, String email,
                                                               String addrName) {  //가상 결제(비경매)
        //가상 결제 페이지 접근 로그 생성
        Long logId = logPageAccess(productsId, email, PageType.VIRTUAL_PAYMENT);

        //product 정보 가져오기
        ProductsEntity product = productsRepository.findByProductsId(productsId);

        //AddrInfoResponseDto addrInfoResponseDto = fetchShippingInfo(email, addrName);
        MembersEntity member = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND)); // 사용자 조회, 없으면 예외 발생

        MembersInfoEntity membersInfoEntity = membersInfoRepository.findByMembersEntity(member);

        //배송지 정보 가져오기
        AddrInfoResponseDto addresses = getShippingInfo(productsId, email, addrName);

        //credit 정보 가져오기
        float currentCredit = membersInfoEntity.getCredit();

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

        logPageExit(logId);

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

    @Transactional
    public VirtualPaymentResponseDto getAucsVirtualPaymentInfo(Long productsId, String email,
                                                               String addrName, int percent) {  //가상 결제(경매)
        //가상 결제 페이지 접근 로그 생성 전에 체크
        LocalDateTime accessTime = LocalDateTime.now();
        if(!isBeforeAuctionEndDate(productsId, accessTime)) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }

        //가상 결제 페이지 접근 로그 생성
        Long logId = logPageAccess(productsId, email, PageType.VIRTUAL_PAYMENT);

        MembersEntity membersEntity = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        MembersInfoEntity membersInfoEntity = membersInfoRepository.findByMembersEntity(membersEntity);

        //product 정보 가져오기
        ProductsEntity product = productsRepository.findByProductsId(productsId);

        //bids에서 실시간 가격 정보를 받아옴
        AucsInfosEntity aucsInfo = aucsInfosRepository.findByProductsEntity(product);
        // BidsCode를 사용하여 현재 응찰 정보를 조회합니다.
        Bids currentBid = bidsRepository.findByBidsCode(aucsInfo.getBidsCode());

        // 현재 응찰 정보가 없는 경우, 시작 가격으로 초기화
        float nowPrice;
        if (currentBid != null) {
            nowPrice = currentBid.getNowPrice();
        } else {
            nowPrice = aucsInfo.getStartPrice();
        }

        //배송지 정보 가져오기
        AddrInfoResponseDto addresses = getShippingInfo(productsId, email, addrName);

        //credit 정보 가져오기
        float currentCredit = membersInfoEntity.getCredit();

        //현재 credit에서 경매 가격을 차감
        float newCredit = currentCredit - nowPrice;

        if (newCredit < 0) {    //사용자의 credit이 결제하려는 금액보다 적을 경우
            throw new AppException(ErrorCode.INSUFFICIENT_CREDIT);
        }

        float newPrice = nowPrice + (nowPrice * (percent / 100));   //응찰가

        //product 이미지 중 대표(첫 번째 url 반환)
        List<ProductImgEntity> images = productImgRepository.findByProductProductsIdOrderByProductImgIdAsc(productsId);
        String image = null;
        if (!images.isEmpty()) {
            image =  images.get(0).getUrl(); // 첫 번째 이미지 URL 반환
        } else {
            image = null; // 이미지가 없으면 null 반환
        }

        //가상 결제 페이지 탈출 로그 생성 전에 체크
        LocalDateTime exitTime = LocalDateTime.now();
        if(!isBeforeAuctionEndDate(productsId, exitTime)) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }

        logPageExit(logId);

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
    @Transactional(readOnly = true)
    public AddrInfoResponseDto getShippingInfo(Long productsId, String email, String addrName) {  //배송지명으로 배송지 조회
        MembersEntity membersEntity = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        MembersInfoEntity membersInfoEntity = membersInfoRepository.findByMembersEntity(membersEntity);

        AddressesEntity addressesEntity = addressesRepository.findByMembersInfoEntityAndAddrName(membersInfoEntity, addrName);

        if (addressesEntity == null) {
            throw new AppException(ErrorCode.SHIPPING_INFO_NOT_FOUND);
        }
        return AddrInfoResponseDto.builder()
                .addrName(addressesEntity.getAddrName())
                .name(addressesEntity.getName())
                .tel(addressesEntity.getTel())
                .zipCode(addressesEntity.getZipNum())
                .addr(addressesEntity.getAddr())
                .addrDetail(addressesEntity.getAddrDetail())
                .build();
    }


    @Override
    @Transactional
    public Long savePayment(MemberDetails principal, PaymentsRequestDto paymentsRequestDto) {    //결제완료
        String email = principal.getMember().getEmail();
        if ("SALE".equals(paymentsRequestDto.getCategory())) {   //비경매
            return saveSalePayment(email, paymentsRequestDto);
        } else if ("AUCS".equals(paymentsRequestDto.getCategory())) {    //경매
            return saveAucsPayment(email, paymentsRequestDto);
        } else {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
    }

    @Transactional
    public Long saveSalePayment(String email, PaymentsRequestDto paymentsRequestDto) {    //결제완료(비경매)
        //결제 페이지 접근 로그 생성
        Long logId = logPageAccess(paymentsRequestDto.getProductsId(), email,
                PageType.PAYMENT_COMPLETED);

        PageAccessLogs accessLog = pageAccessLogsRepository.findById(logId)
                .orElseThrow(() -> new AppException(ErrorCode.LOG_NOT_FOUND)); // 로그를 찾지 못한 경우 예외 발생

        // Orders 정보 저장
        Orders order = Orders.builder()
                .productsId(paymentsRequestDto.getProductsId())
                .email(email)
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
                .zipNum(paymentsRequestDto.getZipNum())
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
        //TODO: 판매자 credit update
        MembersEntity membersEntity = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        MembersInfoEntity membersInfoEntity = membersInfoRepository.findByMembersEntity(membersEntity);

        float currentCredit = membersInfoEntity.getCredit();
        float updatedCredit = currentCredit - paymentsRequestDto.getPrice();
        if (updatedCredit < 0) {    //사용자의 credit이 결제하려는 금액보다 적을 경우
            throw new AppException(ErrorCode.INSUFFICIENT_CREDIT);
        }

        membersInfoEntity.updateCredit(updatedCredit);

        //상품 삭제
        ProductsEntity product = productsRepository.findByProductsId(paymentsRequestDto.getProductsId());
        productsRepository.delete(product);

        //가상 결제 페이지 탈출 로그 생성 전에 체크
        logPageExit(logId);

        return order.getOrdersId();
    }

    @Transactional
    public Long saveAucsPayment(String email, PaymentsRequestDto paymentsRequestDto) {    //결제완료(경매)
        //결제 페이지 접근 로그 생성 전에 체크
        LocalDateTime accessTime = LocalDateTime.now();
        if (!isBeforeAuctionEndDate(paymentsRequestDto.getProductsId(), accessTime)) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }

        //결제 페이지 접근 로그 생성
        Long logId = logPageAccess(paymentsRequestDto.getProductsId(), email,
                PageType.PAYMENT_COMPLETED);

        PageAccessLogs accessLog = pageAccessLogsRepository.findById(logId)
                .orElseThrow(() -> new AppException(ErrorCode.LOG_NOT_FOUND)); // 로그를 찾지 못한 경우 예외 발생


        //3분 연장 판단
        //TODO: AuctionEndDatesEntity 활용
        ProductsEntity product = productsRepository.findByProductsId(paymentsRequestDto.getProductsId());
        AucsInfosEntity aucsInfo = aucsInfosRepository.findByProductsEntity(product);

        LocalDateTime auctionAccessTime = accessLog.getCreatedDate();
        LocalDateTime auctionEndTime = aucsInfo.getEnd(); // 경매의 종료 시간

        long timeDifference = Duration.between(auctionAccessTime, auctionEndTime).toMillis();; // 시간 차이를 밀리초 단위로 계산

        Long orderId = null;
        if (timeDifference >= 3 * 60 * 1000 && timeDifference <= 10 * 60 * 1000) {
            aucsInfo.extendAuctionEndTimeByMinutes(3); // 경매 종료 시간을 3분 연장하는 메소드 호출
            aucsInfosRepository.save(aucsInfo);
            orderId = saveExtendedAuctionOrder(email, paymentsRequestDto, aucsInfo);
        } else if (timeDifference < 3 * 60 * 1000) {
            orderId = saveFinalizedAuctionOrder(email, paymentsRequestDto);
        }

        // Bids 정보 저장
        Bids bid = Bids.builder()
                .productsId(paymentsRequestDto.getProductsId())
                .email(email)
                .nowPrice(paymentsRequestDto.getNowPrice())
                .status(OrderStatus.WAITING_FOR_BID)
                // Bids code는 고유하게 생성하는 로직 필요
                .bidsCode(UUID.randomUUID().toString())
                .build();

        bid = bidsRepository.save(bid);

        // credit 정보 가져오기
        MembersEntity membersEntity = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
        MembersInfoEntity membersInfoEntity = membersInfoRepository.findByMembersEntity(membersEntity);

        float currentCredit = membersInfoEntity.getCredit();
        float updatedCredit = currentCredit - paymentsRequestDto.getPrice();

        if (updatedCredit < 0) {    //사용자의 credit이 결제하려는 금액보다 적을 경우
            throw new AppException(ErrorCode.INSUFFICIENT_CREDIT);
        }

        membersInfoEntity.updateCredit(updatedCredit);

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
                //TODO: 판매자 credit update
                currentCredit = membersInfoEntity.getCredit();
                updatedCredit = currentCredit + refundedAmount; // 현재 credit에서 환불해 줄 금액을 더한 뒤 credit에 반영

                // credit 업데이트 요청
                membersInfoEntity.updateCredit(updatedCredit);

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

        //낙찰일 경우 상품 삭제
        if (timeDifference < 3 * 60 * 1000) {
            //상품 삭제
            product = productsRepository.findByProductsId(paymentsRequestDto.getProductsId());
            productsRepository.delete(product);
        }

        logPageExit(logId);

        return orderId;
    }

    @Transactional
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
    @Transactional
    public Long saveExtendedAuctionOrder(String email, PaymentsRequestDto paymentsRequestDto, AucsInfosEntity aucsInfo) {
        Orders order = Orders.builder()
                .productsId(paymentsRequestDto.getProductsId())
                .email(email)
                .status(OrderStatus.WAITING_FOR_BID)
                .build();
        order = ordersRepository.save(order);
        savePaymentAndDelivery(paymentsRequestDto, order);
        return order.getOrdersId();
    }

    // 경매 종료에 따른 Orders 정보 저장 메서드
    @Transactional
    public Long saveFinalizedAuctionOrder(String email, PaymentsRequestDto paymentsRequestDto) {
        Orders order = Orders.builder()
                .productsId(paymentsRequestDto.getProductsId())
                .email(email)
                .status(OrderStatus.WINNING_BID)
                .build();
        order = ordersRepository.save(order);
        savePaymentAndDelivery(paymentsRequestDto, order);
        return order.getOrdersId();
    }

    // Payment와 Delivery 저장을 위한 공통 메서드
    @Transactional
    public void savePaymentAndDelivery(PaymentsRequestDto paymentsRequestDto, Orders order) {
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


    @Transactional
    // 페이지에서 나갔을 때의 로그 갱신
    public void logPageExit(Long logId) {
        // logId는 페이지 접근 시 저장된 로그의 ID
        PageAccessLogs existingLog = pageAccessLogsRepository.findById(logId)
                .orElseThrow(() -> new AppException(ErrorCode.LOG_NOT_FOUND));
        // BaseTimeEntity의 modifiedDate는 자동으로 갱신
        // JpaRepository의 save 메서드를 호출하여 로그를 갱신
        pageAccessLogsRepository.save(existingLog);
    }

    @Transactional(readOnly = true)
    public boolean isBeforeAuctionEndDate(Long productsId, LocalDateTime dateTimeToCheck) {
        //종료 날짜를 받아와 현재 시간과 비교하여 true 또는 false를 반환
        ProductsEntity product = productsRepository.findByProductsId(productsId);
        AucsInfosEntity aucsInfo = aucsInfosRepository.findByProductsEntity(product);

        LocalDateTime auctionEndDate = aucsInfo.getEnd();

        return dateTimeToCheck.isBefore(auctionEndDate);
    }
}