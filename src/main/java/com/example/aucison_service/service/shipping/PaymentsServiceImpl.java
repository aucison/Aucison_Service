package com.example.aucison_service.service.shipping;


import com.example.aucison_service.dto.payments.AddrInfoResponseDto;
import com.example.aucison_service.dto.payments.PaymentsRequestDto;
import com.example.aucison_service.dto.payments.VirtualPaymentResponseDto;
import com.example.aucison_service.enums.*;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.member.entity.*;
import com.example.aucison_service.jpa.member.repository.*;
import com.example.aucison_service.jpa.product.entity.*;
import com.example.aucison_service.jpa.product.repository.*;
import com.example.aucison_service.jpa.shipping.entity.*;
import com.example.aucison_service.jpa.shipping.repository.*;
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
    private static final Logger logger = LoggerFactory.getLogger(PaymentsServiceImpl.class);
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
   private final BidCountsRepository bidCountsRepository;
   private final HistoriesRepository historiesRepository;
   private final HistoriesImgRepository historiesImgRepository;

    @Autowired
    public PaymentsServiceImpl(BidsRepository bidsRepository, PageAccessLogsRepository pageAccessLogsRepository,
                               OrdersRepository ordersRepository, PaymentsRepository paymentsRepository,
                               DeliveriesRepository deliveriesRepository, RefundsRepository refundsRepository,
                               ProductsRepository productsRepository, MembersRepository membersRepository,
                               MembersInfoRepository membersInfoRepository, AddressesRepository addressesRepository,
                               SaleInfosRepository saleInfosRepository, AucsInfosRepository aucsInfosRepository
                               , ProductImgRepository productImgRepository, BidCountsRepository bidCountsRepository,
                               HistoriesRepository historiesRepository, HistoriesImgRepository historiesImgRepository) {
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
        this.bidCountsRepository = bidCountsRepository;
        this.historiesRepository = historiesRepository;
        this.historiesImgRepository = historiesImgRepository;
    }

    @Override
    @Transactional
    public VirtualPaymentResponseDto getVirtualPaymentInfo(Long productsId, MemberDetails principal, String addrName,
                                                           Optional<Float> bidAmount) {
        ProductsEntity product = productsRepository.findByProductsId(productsId);
        String email = principal.getMember().getEmail();

        if (email.equals(product.getEmail())) {
            throw new AppException(ErrorCode.SELLER_CANNOT_BUY_OWN_PRODUCT);
        }


        switch (product.getCategory()) {
            case "AUCS":
                // bidAmount의 값을 추출하고, 값이 없으면 예외를 발생
                Float actualBidAmount = bidAmount.orElseThrow(() -> new AppException(ErrorCode.INVALID_BIDCOUNT));
                return getAucsVirtualPaymentInfo(productsId, email, addrName, actualBidAmount);
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
        AddrInfoResponseDto addresses = getShippingInfo(email, addrName);

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
                                                               String addrName, Float bidAmount) {  //가상 결제(경매)
        //가상 결제 페이지 접근 로그 생성 전에 체크
        LocalDateTime accessTime = LocalDateTime.now();
        if(!isBeforeAuctionEndDate(productsId, accessTime)) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }

        //가상 결제 페이지 접근 로그 생성
        Long logId = logPageAccess(productsId, email, PageType.VIRTUAL_PAYMENT);

        //product 정보 가져오기
        ProductsEntity product = productsRepository.findByProductsId(productsId);

        MembersEntity membersEntity = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        MembersInfoEntity membersInfoEntity = membersInfoRepository.findByMembersEntity(membersEntity);

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

        // 최소 입찰 금액 및 최대 입찰 금액 계산
        float minBidAmount = nowPrice * 1.03f; // 현재 가격의 3% 증가
        float maxBidAmount = nowPrice * 1.25f; // 현재 가격의 25% 증가

        // bidAmount 검증
        if (bidAmount < minBidAmount || bidAmount > maxBidAmount) {
            throw new AppException(ErrorCode.INVALID_BIDCOUNT);
        }

        //배송지 정보 가져오기
        AddrInfoResponseDto addresses = getShippingInfo(email, addrName);

        //credit 정보 가져오기
        float currentCredit = membersInfoEntity.getCredit();

        //현재 credit에서 경매 가격을 차감
        float newCredit = currentCredit - nowPrice;
        validateCredit(newCredit);

        float newPrice = bidAmount;   //응찰가

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

        VirtualPaymentResponseDto.VirtualPaymentResponseDtoBuilder builder = VirtualPaymentResponseDto.builder()
                .category(product.getCategory())
                .kind(product.getKind())
                .productName(product.getName())
                .productImg(image)
                .price(price)
                .addrName(addresses.getAddrName())
                .name(addresses.getName())
                .tel(addresses.getTel())
                .zipCode(addresses.getZipCode())
                .addr(addresses.getAddr())
                .addrDetail(addresses.getAddrDetail())
                .credit(currentCredit)
                .newCredit(newCredit);

        if ("AUCS".equals(product.getCategory())) {
            AucsInfosEntity aucsInfo = aucsInfosRepository.findByProductsEntity(product);
            builder.end(aucsInfo.getEnd()); // 여기서 product.getEnd()는 경매 마감 시간을 반환한다고 가정합니다.
        }

        return builder.build();
    }

    private AddrInfoResponseDto getShippingInfo(String email, String addrName) {  //배송지명으로 배송지 조회
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
        ProductsEntity product = productsRepository.findByProductsId(paymentsRequestDto.getProductsId());

        if (email.equals(product.getEmail())) {
            throw new AppException(ErrorCode.SELLER_CANNOT_BUY_OWN_PRODUCT);
        }

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
        Orders orders = createOrderAndPaymentAndDelivery(paymentsRequestDto, email, OStatusEnum.COOO);

        // credit 정보 가져오기
        ProductsEntity product = productsRepository.findByProductsId(paymentsRequestDto.getProductsId());

        String buyerEmail = email;
        String sellerEmail = product.getEmail();

        updateMemberCredit(buyerEmail, sellerEmail, paymentsRequestDto.getPrice());

        saveSaleHistory(orders, email, product, paymentsRequestDto);

        //상품 삭제
//        deleteProduct(paymentsRequestDto.getProductsId());
//        updateProductStatus(paymentsRequestDto.getProductsId(), PStatusEnum.C000);

        //가상 결제 페이지 탈출 로그 생성 전에 체크
        logPageExit(logId);

        return orders.getOrdersId();
    }

    private Long saveAucsPayment(String email, PaymentsRequestDto paymentsRequestDto) {    //결제완료(경매)
        Long productId = paymentsRequestDto.getProductsId();

        //결제 페이지 접근 로그 생성 전에 체크
        LocalDateTime accessTime = LocalDateTime.now();
        if (!isBeforeAuctionEndDate(productId, accessTime)) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }

        //결제 페이지 접근 로그 생성
        Long logId = logPageAccess(productId, email, PageType.PAYMENT_COMPLETED);

        PageAccessLogs accessLog = pageAccessLogsRepository.findById(logId)
                .orElseThrow(() -> new AppException(ErrorCode.LOG_NOT_FOUND)); // 로그를 찾지 못한 경우 예외 발생


        //3분 연장 판단
        //TODO: AuctionEndDatesEntity 활용
        ProductsEntity product = productsRepository.findByProductsId(productId);
        AucsInfosEntity aucsInfo = aucsInfosRepository.findByProductsEntity(product);

        LocalDateTime auctionAccessTime = accessLog.getCreatedDate();
        LocalDateTime auctionEndTime = aucsInfo.getEnd(); // 경매의 종료 시간

        long timeDifference = Duration.between(auctionAccessTime, auctionEndTime).toMillis();; // 시간 차이를 밀리초 단위로 계산

        Orders order = null;
        if (timeDifference >= 3 * 60 * 1000 && timeDifference <= 10 * 60 * 1000) {
            aucsInfo.extendAuctionEndTimeByMinutes(3); // 경매 종료 시간을 3분 연장하는 메소드 호출(응찰)
            aucsInfosRepository.save(aucsInfo);
            order = createOrderAndPaymentAndDelivery(paymentsRequestDto, email, OStatusEnum.B001);
            updateProductStatus(productId, PStatusEnum.B000);
        } else if (timeDifference < 3 * 60 * 1000) {    //낙찰
            order = createOrderAndPaymentAndDelivery(paymentsRequestDto, email, OStatusEnum.C001);
            updateProductStatus(productId, PStatusEnum.C000);
        } else {    //응찰
            order = createOrderAndPaymentAndDelivery(paymentsRequestDto, email, OStatusEnum.B001);
            updateProductStatus(productId, PStatusEnum.B000);
        }

        // Bids 정보 저장
        saveBidAndBidCount(paymentsRequestDto, email, order);

        // 구매자 credit update
        //TODO: 판매자 credit update
        String buyerEmail = email;
        String sellerEmail = product.getEmail();
        updateMemberCredit(buyerEmail, sellerEmail, paymentsRequestDto.getPrice());

        //경매 미낙찰에 따른 환불
        //상품 id로 해당 상품 주문 정보를 모두 찾음
        processRefundsForAuction(productId, order, timeDifference);

        saveSaleHistory(order, email, product, paymentsRequestDto);

        //결제 페이지 탈출 로그 생성 전에 체크
        LocalDateTime exitTime = LocalDateTime.now();
        if(!isBeforeAuctionEndDate(paymentsRequestDto.getProductsId(), exitTime)) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }

//        //낙찰일 경우 상품 삭제
//        if (timeDifference < 3 * 60 * 1000) {
//            deleteProduct(product.getProductsId());
//        }

        logPageExit(logId);

        return order.getOrdersId();
    }

    private void saveSaleHistory(Orders orders, String email, ProductsEntity product, PaymentsRequestDto paymentsRequestDto) {
        MembersEntity buyer = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
        MembersInfoEntity membersInfo = membersInfoRepository.findByMembersEntity(buyer);

        // HistoriesEntity 생성 및 저장
        HistoriesEntity history = HistoriesEntity.builder()
                .orderType(OrderType.BUY) // 판매로 설정
                .category(paymentsRequestDto.getCategory()) // 비경매로 설정
                .kind(paymentsRequestDto.getKind()) // 상품 분류 설정
                .productsId(paymentsRequestDto.getProductsId())
                .productName(product.getName()) // 상품명 설정
                .productDetail(product.getInformation()) // 상품 상세 정보 설정
                .price(paymentsRequestDto.getPrice()) // 가격 설정
                .ordersId(orders.getOrdersId()) // 주문번호 설정
                .membersInfoEntity(membersInfo)
                .build();

        history = historiesRepository.save(history);

        // HistoriesImgEntity 생성 및 저장
        // 상품의 첫 번째 이미지 URL을 가져옴
        List<ProductImgEntity> productImages = product.getImages();
        String firstImageUrl = null;
        if (productImages != null && !productImages.isEmpty()) {
            firstImageUrl = productImages.get(0).getUrl(); // 첫 번째 이미지 URL
        }

        // 첫 번째 이미지 URL을 사용하여 HistoriesImgEntity 생성 및 저장
        if (firstImageUrl != null) {
            HistoriesImgEntity historyImg = HistoriesImgEntity.builder()
                    .url(firstImageUrl)
                    .historiesEntity(history)
                    .build();

            historiesImgRepository.save(historyImg);
        }
    }

    private void updateProductStatus(Long productId, PStatusEnum pStatusEnum) {
        ProductsEntity product = productsRepository.findByProductsId(productId);

        if (product == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXIST);
        }

        product.updatePStatus(pStatusEnum);
    }

    private void processRefundsForAuction(Long productId, Orders order, long timeDifference) {
        List<Orders> existingOrders = ordersRepository.findAllByProductsId(productId);

        Orders lastlyOrder = order;    //방금 경매상품을 응찰한 사용자의 주문
        OStatusEnum failedBidStatus = timeDifference < 3 * 60 * 1000 ? OStatusEnum.C002 : OStatusEnum.B002;  //3분 미만일 경우 "패찰", 그 외에 "응찰취소"

        if (!existingOrders.isEmpty()) {

            for (Orders ord : existingOrders) {
                //새로운 응찰이 아니고 "최고가 입찰" 상태였던 이전 주문이라면
                if (lastlyOrder != ord && ord.getOStatus().equals(OStatusEnum.B001)) {

                    //3분 미만일 경우 "패찰", 그 외에 "응찰취소"
                    ord.updateStatus(failedBidStatus);

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
                            .productsId(productId)
                            .email(ord.getEmail())
                            .nowPrice(ord.getPayments().getCost())
                            .oStatus(failedBidStatus)
                            .bidsCode(UUID.randomUUID().toString())
                            .build();
                    bidsRepository.save(failedBid);

                }
            }
        }
    }

    private void saveBidAndBidCount(PaymentsRequestDto paymentsRequestDto, String email, Orders order) {
        // 새로운 응찰 정보 생성
        Bids newBid = Bids.builder()
                .productsId(paymentsRequestDto.getProductsId())
                .email(email)
                .nowPrice(paymentsRequestDto.getPrice()) // 현재 응찰 가격
                .oStatus(order.getOStatus()) // 초기 상태는 '응찰'로 설정
                .bidsCode(UUID.randomUUID().toString()) // 고유한 Bids Code 생성
                .build();

        // Bids 정보 저장
        bidsRepository.save(newBid);

        BidCountsEntity bidCount = bidCountsRepository.findByProductsId(paymentsRequestDto.getProductsId());

        bidCount.plusTotCnt();
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

    private Orders createOrderAndPaymentAndDelivery(PaymentsRequestDto paymentsRequestDto, String email, OStatusEnum status) {
        Orders order = Orders.builder()
                .productsId(paymentsRequestDto.getProductsId())
                .email(email)
                .oStatus(status)
                .build();
        ordersRepository.save(order);

        logger.info(order.getProductsId().toString());

        Payments payment = Payments.builder()
                .cost(paymentsRequestDto.getPrice())
                .orders(order)
                .build();
        paymentsRepository.save(payment);

        logger.info(payment.getPaymentsId().toString());

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

        logger.info(delivery.getDeliveriesId().toString());

        return order;
    }

    private void updateMemberCredit(String buyerEmail, String sellerEmail, float amount) {
        // 구매자 크레딧 차감
        MembersEntity buyer = membersRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
        MembersInfoEntity buyerInfo = membersInfoRepository.findByMembersEntity(buyer);
        float buyerNewCredit = buyerInfo.getCredit() - amount;
        if (buyerNewCredit < 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_CREDIT);
        }
        buyerInfo.updateCredit(buyerNewCredit);

        // 판매자 크레딧 증가
        MembersEntity seller = membersRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
        MembersInfoEntity sellerInfo = membersInfoRepository.findByMembersEntity(seller);
        float sellerNewCredit = sellerInfo.getCredit() + amount;
        sellerInfo.updateCredit(sellerNewCredit);
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

        // 로깅 추가하여 시간 비교 확인
        logger.info("Current Time: {}", dateTimeToCheck);
        logger.info("Auction End Time: {}", auctionEndDate);

        return dateTimeToCheck.isBefore(auctionEndDate);
    }
}