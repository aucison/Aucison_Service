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
import com.example.aucison_service.service.member.GoogleAuthService;
import com.example.aucison_service.service.member.MemberDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentsServiceImpl implements PaymentsService {
    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthService.class);
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
    public VirtualPaymentResponseDto getVirtualPaymentInfo(Long productsId, MemberDetails principal, String addrName,
                                                           Optional<Integer> percent) {
        ProductsEntity product = productsRepository.findByProductsId(productsId);
        String email = principal.getMember().getEmail();

        switch (product.getCategory()) {
            case "AUCS":
                return getAucsVirtualPaymentInfo(productsId, email, addrName, percent.orElseThrow(()
                        -> new AppException(ErrorCode.INVALID_PERCENT)));
            case "SALE":
                return getSaleVirtualPaymentInfo(productsId, email, addrName);
            default:
                throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
    }

    private VirtualPaymentResponseDto getSaleVirtualPaymentInfo(Long productsId, String email,
                                                               String addrName) {  //가상 결제(비경매)
        //가상 결제 페이지 접근 로그 생성
        Long logId = logPageAccess(productsId, email, PageType.VIRTUAL_PAYMENT);

        //product 정보 가져오기
        ProductsEntity product = productsRepository.findByProductsId(productsId);

        MembersEntity member = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND)); // 사용자 조회, 없으면 예외 발생

        MembersInfoEntity membersInfoEntity = membersInfoRepository.findByMembersEntity(member);

        //배송지 정보 가져오기
        AddrInfoResponseDto addresses = getShippingInfo(productsId, email, addrName);

        //credit 정보 가져오기
        float currentCredit = membersInfoEntity.getCredit();

        //등록 가격(비경매) 가져오기
        SaleInfosEntity saleInfosEntity = saleInfosRepository.findByProductsEntity(product);
        float newCredit = currentCredit - saleInfosEntity.getPrice();        //현재 credit에서 등록 가격을 차감

        validateCredit(newCredit);

        String image = fetchProductImage(productsId);

        logPageExit(logId);

        return buildVirtualPaymentResponseDto(product, image, saleInfosEntity.getPrice(), addresses,
                currentCredit, newCredit);

    }

    private VirtualPaymentResponseDto getAucsVirtualPaymentInfo(Long productsId, String email,
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
        validateCredit(newCredit);

        float newPrice = nowPrice + (nowPrice * (percent / 100));   //응찰가

        //product 이미지 중 대표(첫 번째 url 반환)
        String image = fetchProductImage(productsId);

        //가상 결제 페이지 탈출 로그 생성 전에 체크
        LocalDateTime exitTime = LocalDateTime.now();
        if(!isBeforeAuctionEndDate(productsId, exitTime)) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }

        logPageExit(logId);

        return buildVirtualPaymentResponseDto(product, image, newPrice, addresses,
                currentCredit, newCredit);
    }

    private void validateCredit(float credit) { //사용자의 credit이 결제하려는 금액보다 적은지 검사하는 메서드
        if (credit < 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_CREDIT);
        }
    }

    private String fetchProductImage(Long productsId) { //product 이미지 중 대표(첫 번째 url 반환)
        List<ProductImgEntity> images = productImgRepository.findByProductProductsIdOrderByProductImgIdAsc(productsId);
        return images.isEmpty() ? null : images.get(0).getUrl();
    }

    //가상결제 공통반환 메서드
    private VirtualPaymentResponseDto buildVirtualPaymentResponseDto(ProductsEntity product,
                                                                     String image,
                                                                     float price,
                                                                     AddrInfoResponseDto addresses,
                                                                     float currentCredit,
                                                                     float newCredit) {
        return VirtualPaymentResponseDto.builder()
                .category(product.getCategory())
                .name(product.getName())
                .productImg(image)
                .price(price)
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

        switch (paymentsRequestDto.getCategory()) {
            case "AUCS":
                return saveAucsPayment(email, paymentsRequestDto);
            case "SALE":
                return saveSalePayment(email, paymentsRequestDto);
            default:
                throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
    }

    private Long saveSalePayment(String email, PaymentsRequestDto paymentsRequestDto) {    //결제완료(비경매)
        //결제 페이지 접근 로그 생성
        Long logId = logPageAccess(paymentsRequestDto.getProductsId(), email,
                PageType.PAYMENT_COMPLETED);

        pageAccessLogsRepository.findById(logId).orElseThrow(() -> new AppException(ErrorCode.LOG_NOT_FOUND)); // 로그를 찾지 못한 경우 예외 발생

        // Orders, Payments, Deliveries 정보 저장
        Orders orders = createOrderAndPaymentAndDelivery(paymentsRequestDto, email, OrderStatus.ORDER_COMPLETED);

        // credit 정보 가져오기
        //TODO: 판매자 credit update
        updateMemberCredit(email, paymentsRequestDto.getPrice());

        //상품 삭제
        deleteProduct(paymentsRequestDto.getProductsId());

        //가상 결제 페이지 탈출 로그 생성 전에 체크
        logPageExit(logId);

        return orders.getOrdersId();
    }

    private Long saveAucsPayment(String email, PaymentsRequestDto paymentsRequestDto) {    //결제완료(경매)
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

        Orders order = null;
        if (timeDifference >= 3 * 60 * 1000 && timeDifference <= 10 * 60 * 1000) {
            aucsInfo.extendAuctionEndTimeByMinutes(3); // 경매 종료 시간을 3분 연장하는 메소드 호출(응찰)
            aucsInfosRepository.save(aucsInfo);
            order = createOrderAndPaymentAndDelivery(paymentsRequestDto, email, OrderStatus.WAITING_FOR_BID);
        } else if (timeDifference < 3 * 60 * 1000) {    //낙찰
            order = createOrderAndPaymentAndDelivery(paymentsRequestDto, email, OrderStatus.WINNING_BID);
        } else {    //응찰
            order = createOrderAndPaymentAndDelivery(paymentsRequestDto, email, OrderStatus.WAITING_FOR_BID);
        }

        // Bids 정보 저장
        saveBidInfo(paymentsRequestDto, email, order);

        // 구매자 credit update
        //TODO: 판매자 credit update
        updateMemberCredit(email, paymentsRequestDto.getPrice());

        //경매 미낙찰에 따른 환불
        //상품 id로 해당 상품 주문 정보를 모두 찾음
        List<Orders> existingOrders = ordersRepository.findAllByProductsId(paymentsRequestDto.getProductsId());
        logger.info(String.valueOf(existingOrders.size()));

//        Orders winningOrder = order;
        if (!existingOrders.isEmpty()) {
            //logger.info(winningOrder.getPayments().getPaymentsId().toString());

            for (Orders ord : existingOrders) {
                logger.info(ord.getOrdersId().toString());
                //새로운 주문이 아니고 "응찰" 상태였던 이전 주문이라면
                if (ord.getStatus().equals(OrderStatus.WAITING_FOR_BID)) {
                    logger.info(ord.getPayments().getPaymentsId().toString());

                    ord.updateStatus(OrderStatus.FAILED_BID);   //이전 응찰은 "패찰"로 변해야 함

                    float refundedAmount = ord.getPayments().getCost();   //환불해 줄 금액

                    //credit 정보 가져오기
                    //TODO: 판매자 credit update
                    MembersEntity member = membersRepository.findByEmail(order.getEmail())
                            .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
                    MembersInfoEntity memberInfo = membersInfoRepository.findByMembersEntity(member);

                    float currentCredit = memberInfo.getCredit();
                    float updatedCredit = currentCredit + refundedAmount; // 현재 credit에서 환불해 줄 금액을 더한 뒤 credit에 반영

                    // credit 업데이트 요청
                    memberInfo.updateCredit(updatedCredit);

                    // 환불 정보 저장
                    Refunds refund = Refunds.builder()
                            .cost(refundedAmount)
                            .orders(ord)
                            .build();
                    refundsRepository.save(refund);

                    // 실시간 응찰 내역에 패찰 정보 저장
                    Bids failedBid = Bids.builder()
                            .productsId(paymentsRequestDto.getProductsId())
                            .email(ord.getEmail())
                            .nowPrice(ord.getPayments().getCost())
                            .status(OrderStatus.FAILED_BID)
                            .bidsCode(UUID.randomUUID().toString())
                            .build();
                    bidsRepository.save(failedBid);

                }
            }
        }

        //결제 페이지 탈출 로그 생성 전에 체크
        LocalDateTime exitTime = LocalDateTime.now();
        if(!isBeforeAuctionEndDate(paymentsRequestDto.getProductsId(), exitTime)) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }

        //낙찰일 경우 상품 삭제
        if (timeDifference < 3 * 60 * 1000) {
            deleteProduct(product.getProductsId());
        }

        logPageExit(logId);

        return order.getOrdersId();
    }

    private void saveBidInfo(PaymentsRequestDto paymentsRequestDto, String email, Orders order) {
        // 새로운 응찰 정보 생성
        Bids newBid = Bids.builder()
                .productsId(paymentsRequestDto.getProductsId())
                .email(email)
                .nowPrice(paymentsRequestDto.getPrice()) // 현재 응찰 가격
                .status(order.getStatus()) // 초기 상태는 '응찰'로 설정
                .bidsCode(UUID.randomUUID().toString()) // 고유한 Bids Code 생성
                .build();

        // Bids 정보 저장
        bidsRepository.save(newBid);
    }

    // 페이지에 접근했을 때의 로그 생성
    private Long logPageAccess(Long productId, String email, PageType pageType) {
        PageAccessLogs log = PageAccessLogs.builder()
                .productsId(productId)
                .email(email)
                .pageType(pageType)
                .build();

        log = pageAccessLogsRepository.save(log);
        return log.getPageAccessLogsId();
    }

    private Orders createOrderAndPaymentAndDelivery(PaymentsRequestDto paymentsRequestDto, String email, OrderStatus status) {
        Orders order = Orders.builder()
                .productsId(paymentsRequestDto.getProductsId())
                .email(email)
                .status(status)
                .build();
        ordersRepository.save(order);

        Payments payment = Payments.builder()
                .cost(paymentsRequestDto.getPrice())
                .orders(order)
                .build();
        paymentsRepository.save(payment);

        Deliveries delivery = Deliveries.builder()
                .addr(paymentsRequestDto.getAddr())
                .addrDetail(paymentsRequestDto.getAddrDetail())
                .addrName(paymentsRequestDto.getAddrName())
                .zipNum(paymentsRequestDto.getZipNum())
                .isCompleted(false)
                .isStarted(false)
                .name(paymentsRequestDto.getName())
                .tel(paymentsRequestDto.getTel())
                .orders(order)
                .build();
        deliveriesRepository.save(delivery);

        return order;
    }

    private void updateMemberCredit(String email, float amount) {
        MembersEntity membersEntity = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
        MembersInfoEntity membersInfoEntity = membersInfoRepository.findByMembersEntity(membersEntity);
        float newCredit = membersInfoEntity.getCredit() - amount;
        if (newCredit < 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_CREDIT);
        }
        membersInfoEntity.updateCredit(newCredit);
    }

    private void deleteProduct(Long productId) {
        ProductsEntity product = productsRepository.findByProductsId(productId);
        productsRepository.delete(product);
    }


    // 페이지에서 나갔을 때의 로그 갱신
    private void logPageExit(Long logId) {
        // logId는 페이지 접근 시 저장된 로그의 ID
        PageAccessLogs existingLog = pageAccessLogsRepository.findById(logId)
                .orElseThrow(() -> new AppException(ErrorCode.LOG_NOT_FOUND));
        // BaseTimeEntity의 modifiedDate는 자동으로 갱신
        // JpaRepository의 save 메서드를 호출하여 로그를 갱신
        pageAccessLogsRepository.save(existingLog);
    }

    private boolean isBeforeAuctionEndDate(Long productsId, LocalDateTime dateTimeToCheck) {
        //종료 날짜를 받아와 현재 시간과 비교하여 true 또는 false를 반환
        ProductsEntity product = productsRepository.findByProductsId(productsId);
        AucsInfosEntity aucsInfo = aucsInfosRepository.findByProductsEntity(product);

        LocalDateTime auctionEndDate = aucsInfo.getEnd();

        return dateTimeToCheck.isBefore(auctionEndDate);
    }
}