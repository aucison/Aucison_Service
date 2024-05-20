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
import com.example.aucison_service.service.product.ProductService;
import com.nimbusds.oauth2.sdk.util.StringUtils;
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
   private final ProductService productService;

    @Autowired
    public PaymentsServiceImpl(BidsRepository bidsRepository, PageAccessLogsRepository pageAccessLogsRepository,
                               OrdersRepository ordersRepository, PaymentsRepository paymentsRepository,
                               DeliveriesRepository deliveriesRepository, RefundsRepository refundsRepository,
                               ProductsRepository productsRepository, MembersRepository membersRepository,
                               MembersInfoRepository membersInfoRepository, AddressesRepository addressesRepository,
                               SaleInfosRepository saleInfosRepository, AucsInfosRepository aucsInfosRepository
                               , ProductImgRepository productImgRepository, BidCountsRepository bidCountsRepository,
                               HistoriesRepository historiesRepository, HistoriesImgRepository historiesImgRepository, ProductService productService) {
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
        this.productService = productService;
    }

    @Override
    @Transactional
    public VirtualPaymentResponseDto getVirtualPaymentInfo(Long productsId, MemberDetails principal, Optional<Float> bidAmount) {
        String email = principal.getMember().getEmail();

        ProductsEntity product = productsRepository.findByProductsId(productsId);
        if (product == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        if (email.equals(product.getEmail())) {
            throw new AppException(ErrorCode.SELLER_CANNOT_BUY_OWN_PRODUCT);
        }


        switch (product.getCategory()) {
            case "AUCS":
                // bidAmount의 값을 추출하고, 값이 없으면 예외를 발생
                Float actualBidAmount = bidAmount.orElseThrow(() -> new AppException(ErrorCode.INVALID_BIDCOUNT));
                return getAucsVirtualPaymentInfo(product, email, actualBidAmount);
            case "SALE":
                return getSaleVirtualPaymentInfo(product, email);
            default:
                throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
    }

    private VirtualPaymentResponseDto getSaleVirtualPaymentInfo(ProductsEntity product, String email) {  //가상 결제(비경매)
        Long productsId = product.getProductsId();

        //가상 결제 페이지 접근 로그 생성
        Long logId = logPageAccess(productsId, email, PageType.VIRTUAL_PAYMENT);

        //member 정보 가져오기
        MembersEntity member = membersRepository.findByEmail(email);
        if (member == null) {
            throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
        }

        MembersInfoEntity membersInfo = membersInfoRepository.findByMembersEntity(member);
        if (membersInfo == null) {
            throw new AppException(ErrorCode.MEMBERS_INFO_NOT_FOUND);
        }

        //배송지 정보 가져오기
        AddrInfoResponseDto addresses = getShippingInfo(email);

        //credit 정보 가져오기
        float currentCredit = membersInfo.getCredit();

        //등록 가격(비경매) 가져오기
        SaleInfosEntity saleInfosEntity = saleInfosRepository.findByProductsEntity(product);
        float newCredit = currentCredit - saleInfosEntity.getPrice();        //현재 credit에서 등록 가격을 차감

        //사용자의 credit이 결제하려는 금액보다 적은지 검사
        validateCredit(newCredit);

        //product 이미지 중 대표(첫 번째 url 반환)
        String image = fetchProductImage(productsId);

        logPageExit(logId);

        return buildVirtualPaymentResponseDto(product, image, saleInfosEntity.getPrice(), addresses,
                currentCredit, newCredit);

    }

    private VirtualPaymentResponseDto getAucsVirtualPaymentInfo(ProductsEntity product, String email, Float bidAmount) {  //가상 결제(경매)
        Long productsId = product.getProductsId();

        //가상 결제 페이지 접근 로그 생성 전에 체크
        LocalDateTime accessTime = LocalDateTime.now();
        if(!isBeforeAuctionEndDate(productsId, accessTime)) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }

        //가상 결제 페이지 접근 로그 생성
        Long logId = logPageAccess(productsId, email, PageType.VIRTUAL_PAYMENT);

        //사용자 정보 가져오기
        MembersEntity member = membersRepository.findByEmail(email);
        if (member == null) {
            throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
        }

        MembersInfoEntity membersInfo = membersInfoRepository.findByMembersEntity(member);
        if (membersInfo == null) {
            throw new AppException(ErrorCode.MEMBERS_INFO_NOT_FOUND);
        }

        //aucs_info에서 실시간 가격 정보를 받아옴
        AucsInfosEntity aucsInfo = aucsInfosRepository.findByProductsEntity(product);
        Float nowPrice = null;
        if (aucsInfo == null) {
            throw new AppException(ErrorCode.AUCS_PRODUCT_NOT_EXIST);
        } else {
            nowPrice = aucsInfo.getHighestPrice();
        }

        // 최소 입찰 금액 및 최대 입찰 금액 계산
//        float minBidAmount = nowPrice * 1.03f; // 현재 가격의 3% 증가
//        float maxBidAmount = nowPrice * 1.25f; // 현재 가격의 25% 증가

        // bidAmount 검증
//        if (bidAmount < minBidAmount || bidAmount > maxBidAmount) {
//            throw new AppException(ErrorCode.INVALID_BIDCOUNT);
//        }

        //배송지 정보 가져오기
        AddrInfoResponseDto addresses = getShippingInfo(email);

        //credit 정보 가져오기
        float currentCredit = membersInfo.getCredit();

        //현재 credit에서 경매 가격을 차감
        float newCredit = currentCredit - bidAmount;
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
                .zipNum(addresses.getZipCode())
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

    private AddrInfoResponseDto getShippingInfo(String email) {  //대표 배송지 조회
        MembersEntity member = membersRepository.findByEmail(email);
        if (member == null) {
            throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
        }

        MembersInfoEntity membersInfo = membersInfoRepository.findByMembersEntity(member);
        if (membersInfo == null) {
            throw new AppException(ErrorCode.MEMBERS_INFO_NOT_FOUND);
        }

        AddressesEntity addresses = addressesRepository.findByMembersInfoEntityAndIsPrimary(membersInfo, true);
        if (addresses == null) {
            throw new AppException(ErrorCode.ADDRESS_NOT_FOUND);
        }

        return AddrInfoResponseDto.builder()
                .addrName(addresses.getAddrName())
                .name(addresses.getName())
                .tel(addresses.getTel())
                .zipCode(addresses.getZipNum())
                .addr(addresses.getAddr())
                .addrDetail(addresses.getAddrDetail())
                .build();
    }


    @Override
    @Transactional
    public Long savePayment(MemberDetails principal, PaymentsRequestDto paymentsRequestDto) {    //결제완료
        //paymentRequestDto 검증
        validatePaymentsRequestDto(paymentsRequestDto);

        String email = principal.getMember().getEmail();

        ProductsEntity product = productsRepository.findByProductsId(paymentsRequestDto.getProductsId());
        if (product == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

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

    @Override
    public void saveAucsPaymentInfo(String email, PaymentsRequestDto paymentsRequestDto) {  //스케줄링에 의해 호출될 낙찰(결제완료)메소드
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

        //order/payments/deliveries 저장(o_status의 C001 "낙찰")
        Orders order = createOrderAndPaymentAndDelivery(paymentsRequestDto, email, OStatusEnum.C001);

        ProductsEntity product = productsRepository.findByProductsId(productId);

        //판매자 histories p_status 업데이트(p_status의 C000 "판매완료")
        HistoriesEntity history = historiesRepository.findByNameAndOrderType(product.getName(), OrderType.SELL);
        history.updatePstatus(PStatusEnum.C000);

        //구매자 credit 감소, 판매자 credit 증가

        String buyerEmail = email;
        String sellerEmail = product.getEmail();

        updateBuyerCredit(buyerEmail, paymentsRequestDto.getPrice());
        updateSellerCredit(sellerEmail, paymentsRequestDto.getPrice());

        // Bids 정보 저장
        saveBidAndBidCount(paymentsRequestDto, email, order);

        //경매 미낙찰에 따른 환불
        List<Orders> existingOrders = ordersRepository.findAllByProductsId(productId);

        Orders lastlyOrder = order;    //방금 경매상품을 응찰한 사용자의 주문
        OStatusEnum failedBidStatus = OStatusEnum.C002;  //패찰

        if (!existingOrders.isEmpty()) {    //최초 응찰이 아님을 판단

            for (Orders ord : existingOrders) {
                //새로운 응찰이 아니고 "최고가 입찰" 상태였던 이전 주문이라면
                if (ord != lastlyOrder && ord.getOStatus().equals(OStatusEnum.B001)) {

                    //3분 미만일 경우 "패찰", 그 외에 "응찰취소"
                    //"최고가 입찰" 직전의 "최고가 입찰" 사용자의 orders, histories의 o_status 업데이트(B002 "응찰취소")
                    ord.updateStatus(failedBidStatus);

                     history = historiesRepository.findByOrdersId(ord.getOrdersId());
                     history.updateOstatus(failedBidStatus);

                    float refundedAmount = ord.getPayments().getCost();   //환불해 줄 금액

                    //credit 정보 가져오기
                    //"최고가 입찰" 직전의 "최고가 입찰" 사용자의 credit 증가(환불)
                    MembersEntity member = membersRepository.findByEmail(ord.getEmail());
                    if (member == null) {
                        throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
                    }

                    MembersInfoEntity membersInfo = membersInfoRepository.findByMembersEntity(member);
                    if (membersInfo == null) {
                        throw new AppException(ErrorCode.MEMBERS_INFO_NOT_FOUND);
                    }

                    float currentCredit = membersInfo.getCredit();
                    float updatedCredit = currentCredit + refundedAmount; // 현재 credit에서 환불해 줄 금액을 더한 뒤 credit에 반영

                    membersInfo.updateCredit(updatedCredit);    // credit 업데이트 요청

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

        //구매자 histories/historiesImgEntity정보 저장
        saveSaleHistory(order, email, product, paymentsRequestDto);


        //결제 페이지 탈출 로그 생성 전에 체크
        LocalDateTime exitTime = LocalDateTime.now();
        if(!isBeforeAuctionEndDate(paymentsRequestDto.getProductsId(), exitTime)) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }

//        TODO: 낙찰일 경우 상품 삭제
//        if (timeDifference < 3 * 60 * 1000) {
//            deleteProduct(product.getProductsId());
//        }

        logPageExit(logId);
    }

    private void validatePaymentsRequestDto(PaymentsRequestDto dto) {
        // StringUtils.isBlank 체크는 null, 빈 문자열, 공백만 있는 경우를 모두 체크합니다.
        //내부적 검증이므로(가상결제 조회와 정보가 연동되어야 하기 때문) IllegalArgumentException 표준 예외 사용
        if (dto.getProductsId() == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (StringUtils.isBlank(dto.getCategory())) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        if (StringUtils.isBlank(dto.getKind())) {
            throw new IllegalArgumentException("Kind cannot be null or empty");
        }
        if (StringUtils.isBlank(dto.getAddrName())) {
            throw new IllegalArgumentException("Address Name cannot be null or empty");
        }
        if (StringUtils.isBlank(dto.getZipNum())) {
            throw new IllegalArgumentException("Zip Number cannot be null or empty");
        }
        if (StringUtils.isBlank(dto.getAddr())) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }
        if (StringUtils.isBlank(dto.getAddrDetail())) {
            throw new IllegalArgumentException("Address Detail cannot be null or empty");
        }
        if (StringUtils.isBlank(dto.getName())) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (StringUtils.isBlank(dto.getTel())) {
            throw new IllegalArgumentException("Telephone cannot be null or empty");
        }
        if (dto.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }

    private Long saveSalePayment(String email, PaymentsRequestDto paymentsRequestDto) {    //결제완료(비경매)
        //결제 페이지 접근 로그 생성
        Long logId = logPageAccess(paymentsRequestDto.getProductsId(), email,
                PageType.PAYMENT_COMPLETED);

        pageAccessLogsRepository.findById(logId).orElseThrow(() -> new AppException(ErrorCode.LOG_NOT_FOUND)); // 로그를 찾지 못한 경우 예외 발생

        // Orders, Payments, Deliveries 정보 저장
        Orders orders = createOrderAndPaymentAndDelivery(paymentsRequestDto, email, OStatusEnum.C000);

        //구매자 credit 감소, 판매자 credit 증가
        ProductsEntity product = productsRepository.findByProductsId(paymentsRequestDto.getProductsId());

        String buyerEmail = email;
        String sellerEmail = product.getEmail();

        updateBuyerCredit(buyerEmail, paymentsRequestDto.getPrice());
        updateSellerCredit(sellerEmail, paymentsRequestDto.getPrice());

        //histories/historiesImgEntity정보 저장(구매자)
        saveSaleHistory(orders, email, product, paymentsRequestDto);

        //판매자 histories p_status 업데이트(p_status의 C000 "판매완료")
        HistoriesEntity history = historiesRepository.findByNameAndOrderType(product.getName(), OrderType.SELL);
        history.updatePstatus(PStatusEnum.C000);


//        updateSoldDate(product);    //TODO: 삭제될 로직

        //product_delete 호출하여 상품 삭제
        logger.info("일반상품 판매완료로 인한 상품 삭제 : 상품명 - " + product.getName() );
        productService.deleteSaleProduct(product.getProductsId());

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

        //상품 정보 가져오기
        ProductsEntity product = productsRepository.findByProductsId(productId);
        AucsInfosEntity aucsInfo = aucsInfosRepository.findByProductsEntity(product);

        LocalDateTime auctionAccessTime = accessLog.getCreatedDate();
        LocalDateTime auctionEndTime = aucsInfo.getEnd(); // 경매의 종료 시간

        long timeDifference = Duration.between(auctionAccessTime, auctionEndTime).toMillis();; // 시간 차이를 밀리초 단위로 계산

        Orders order = null;

        String buyerEmail = email;
        String sellerEmail = product.getEmail();

        HistoriesEntity history = historiesRepository.findByNameAndOrderType(product.getName(), OrderType.SELL);

        if (timeDifference >= 3 * 60 * 1000 && timeDifference <= 10 * 60 * 1000) {  //응찰(3분 연장)
            // aucs_info의 end 업데이트(3분 연장 시)
            aucsInfo.extendAuctionEndTimeByMinutes(3);
            aucsInfosRepository.save(aucsInfo);

            //order(o_status의 B001 "최고가 입찰")/payments/deliveries 저장
            order = createOrderAndPaymentAndDelivery(paymentsRequestDto, email, OStatusEnum.B001);

            //product의 p_status 업데이트(p_status의 B000 "응찰중")
            updateProductStatus(productId, PStatusEnum.B000);

            //판매자 histories의 p_status 업데이트(p_status의 B000 "응찰중")
            history.updatePstatus(PStatusEnum.B000);

            //구매자 credit 감소
            updateBuyerCredit(buyerEmail, paymentsRequestDto.getPrice());
        } else if (timeDifference < 3 * 60 * 1000) {    //낙찰
            //order/payments/deliveries 저장(o_status의 C001 "낙찰")
            order = createOrderAndPaymentAndDelivery(paymentsRequestDto, email, OStatusEnum.C001);

            //TODO:  product는 삭제될 예정이라 필요없음
//            updateProductStatus(productId, PStatusEnum.C000);
//            updateSoldDate(product);

            //판매자 histories p_status 업데이트(p_status의 C000 "판매완료")
            history.updatePstatus(PStatusEnum.C000);

            //구매자 credit 감소, 판매자 credit 증가
            updateBuyerCredit(buyerEmail, paymentsRequestDto.getPrice());
            updateSellerCredit(sellerEmail, paymentsRequestDto.getPrice());
        } else {    //응찰
            //order(o_status의 B001 "최고가 입찰")/payments/deliveries 저장
            order = createOrderAndPaymentAndDelivery(paymentsRequestDto, email, OStatusEnum.B001);

            //product의 p_status 업데이트(p_status의 B000 "응찰중")
            updateProductStatus(productId, PStatusEnum.B000);

            //판매자 histories의 p_status 업데이트(p_status의 B000 "응찰중")
            history.updatePstatus(PStatusEnum.B000);

            //구매자 credit 감소
            updateBuyerCredit(buyerEmail, paymentsRequestDto.getPrice());
        }

        // Bids 정보 저장
        saveBidAndBidCount(paymentsRequestDto, email, order);

        //경매상품 최고가 현재 응찰가로 업데이트
        aucsInfo.updateHighestPrice(paymentsRequestDto.getPrice());

        //경매 미낙찰에 따른 환불
        //상품 id로 해당 상품 주문 정보를 모두 찾음
        processRefundsForAuction(productId, order, timeDifference);

        //구매자 histories/historiesImgEntity정보 저장
        saveSaleHistory(order, email, product, paymentsRequestDto);

        //결제 페이지 탈출 로그 생성 전에 체크
        LocalDateTime exitTime = LocalDateTime.now();
        if(!isBeforeAuctionEndDate(paymentsRequestDto.getProductsId(), exitTime)) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }

//        TODO: 낙찰일 경우 상품 삭제
//        if (timeDifference < 3 * 60 * 1000) {
//            productService.deleteAucsProduct();
//        }

        logPageExit(logId);

        return order.getOrdersId();
    }

//    private void updateSoldDate(ProductsEntity product) {
//        HistoriesEntity history = historiesRepository.findByProductsIdAndEmail(product.getProductsId(), product.getEmail());
//        if (history == null) {
//            throw new AppException(ErrorCode.HISTORY_NOT_FOUND);
//        } else {
//            history.updateSoldDate(LocalDateTime.now());
//        }
//    }

    private void saveSaleHistory(Orders orders, String email, ProductsEntity product, PaymentsRequestDto paymentsRequestDto) {
        MembersEntity buyer = membersRepository.findByEmail(email);
        if (buyer == null) {
            throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
        }

        MembersInfoEntity membersInfo = membersInfoRepository.findByMembersEntity(buyer);
        if (membersInfo == null) {
            throw new AppException(ErrorCode.MEMBERS_INFO_NOT_FOUND);
        }

        HistoriesEntity history = null;
        if (product.getCategory().equals("AUCS")) {
            // HistoriesEntity 생성 및 저장
            history = HistoriesEntity.builder()
                    .orderType(OrderType.BUY) // 구매로 설정
                    .name(product.getName())
                    .category(product.getCategory())
                    .kind(product.getKind())
                    .highestPrice(paymentsRequestDto.getPrice())
                    .oStatus(orders.getOStatus())
                    .ordersId(orders.getOrdersId())
                    .productsId(product.getProductsId())
                    .membersInfoEntity(membersInfo)
                    .build();

            history = historiesRepository.save(history);
        }

        if (product.getCategory().equals("SALE")) {
            // HistoriesEntity 생성 및 저장
            history = HistoriesEntity.builder()
                    .orderType(OrderType.BUY) // 구매로 설정
                    .name(product.getName())
                    .category(product.getCategory())
                    .kind(product.getKind())
                    .salePrice(paymentsRequestDto.getPrice())
                    .oStatus(orders.getOStatus())
                    .ordersId(orders.getOrdersId()) // 주문번호 설정
                    .productsId(product.getProductsId())
                    .membersInfoEntity(membersInfo)
                    .build();

            history = historiesRepository.save(history);
        }

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

        if (!existingOrders.isEmpty()) {    //최초 응찰이 아님을 판단

            for (Orders ord : existingOrders) {
                //새로운 응찰이 아니고 "최고가 입찰" 상태였던 이전 주문이라면
                if (ord != lastlyOrder && ord.getOStatus().equals(OStatusEnum.B001)) {

                    //3분 미만일 경우 "패찰", 그 외에 "응찰취소"
                    //histories의 o_status 업데이트
                    ord.updateStatus(failedBidStatus);

                    HistoriesEntity history = historiesRepository.findByOrdersId(ord.getOrdersId());
                    history.updateOstatus(failedBidStatus);

                    float refundedAmount = ord.getPayments().getCost();   //환불해 줄 금액

                    //credit 정보 가져오기
                    //"최고가 입찰" 직전의 "최고가 입찰" 사용자의 credit 증가(환불)
                    MembersEntity member = membersRepository.findByEmail(ord.getEmail());
                    if (member == null) {
                        throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
                    }

                    MembersInfoEntity membersInfo = membersInfoRepository.findByMembersEntity(member);
                    if (membersInfo == null) {
                        throw new AppException(ErrorCode.MEMBERS_INFO_NOT_FOUND);
                    }

                    float currentCredit = membersInfo.getCredit();
                    float updatedCredit = currentCredit + refundedAmount; // 현재 credit에서 환불해 줄 금액을 더한 뒤 credit에 반영

                    membersInfo.updateCredit(updatedCredit);    // credit 업데이트 요청

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

//        logger.info(order.getProductsId().toString());

        Payments payment = Payments.builder()
                .cost(paymentsRequestDto.getPrice())
                .orders(order)
                .build();
        paymentsRepository.save(payment);

//        logger.info(payment.getPaymentsId().toString());

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

//        logger.info(delivery.getDeliveriesId().toString());

        return order;
    }

    private void updateBuyerCredit(String buyerEmail, float amount) {
        // 구매자 크레딧 차감
        MembersEntity buyer = membersRepository.findByEmail(buyerEmail);
        if (buyer == null) {
            throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
        }

        MembersInfoEntity buyerInfo = membersInfoRepository.findByMembersEntity(buyer);
        if (buyerInfo == null) {
            throw new AppException(ErrorCode.MEMBERS_INFO_NOT_FOUND);
        }

        float buyerNewCredit = buyerInfo.getCredit() - amount;
        if (buyerNewCredit < 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_CREDIT);
        }
        buyerInfo.updateCredit(buyerNewCredit);
    }
    private void updateSellerCredit(String sellerEmail, float amount) {
        // 판매자 크레딧 증가
        MembersEntity seller = membersRepository.findByEmail(sellerEmail);
        if (seller == null) {
            throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
        }

        MembersInfoEntity sellerInfo = membersInfoRepository.findByMembersEntity(seller);
        if (sellerInfo == null) {
            throw new AppException(ErrorCode.MEMBERS_INFO_NOT_FOUND);
        }

        float sellerNewCredit = sellerInfo.getCredit() + amount;
        sellerInfo.updateCredit(sellerNewCredit);
    }

//    private void deleteProduct(Long productId) {
//        ProductsEntity product = productsRepository.findByProductsId(productId);
//        productsRepository.delete(product);
//    }


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