package com.example.aucison_service.service.member;


import com.example.aucison_service.dto.mypage.*;
import com.example.aucison_service.enums.Category;
import com.example.aucison_service.enums.OrderStatus;
import com.example.aucison_service.enums.OrderType;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.member.*;
import com.example.aucison_service.jpa.product.ProductsEntity;
import com.example.aucison_service.jpa.product.ProductsRepository;
import com.example.aucison_service.jpa.shipping.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MypageServiceImpl implements MypageService {

    private final HistoriesRepository historiesRepository;
    private final HistoriesImgRepository historiesImgRepository;
    private final MembersInfoRepository membersInfoRepository;
    private final MembersRepository membersRepository;
    private final OrdersRepository ordersRepository;
    private final AuctionEndDatesRepository auctionEndDatesRepository;
    private  final DeliveriesRepository deliveriesRepository;
    private final BidsRepository bidsRepository;
    private final ProductsRepository productsRepository;
    private final AddressesRepository addressesRepository;

    @Autowired
    public MypageServiceImpl(HistoriesRepository historiesRepository, HistoriesImgRepository historiesImgRepository,
                             MembersInfoRepository membersInfoRepository, MembersRepository membersRepository,
                             OrdersRepository ordersRepository, AuctionEndDatesRepository auctionEndDatesRepository,
                             DeliveriesRepository deliveriesRepository, BidsRepository bidsRepository,
                             ProductsRepository productsRepository, AddressesRepository addressesRepository) {
        this.historiesRepository = historiesRepository;
        this.historiesImgRepository = historiesImgRepository;
        this.membersInfoRepository = membersInfoRepository;
        this.membersRepository = membersRepository;
        this.ordersRepository = ordersRepository;
        this.auctionEndDatesRepository = auctionEndDatesRepository;
        this.deliveriesRepository = deliveriesRepository;
        this.bidsRepository = bidsRepository;
        this.productsRepository = productsRepository;
        this.addressesRepository = addressesRepository;
    }

    //orElseThrow는 entity에 직접 적용할 수 없고, Optional 객체에 사용되어야 한다.
    //판매 상품 조회
    @Override
    public List<ResponseOrderHistoryDto> getOrderInfo(String email) {
        MembersEntity members = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND)); // 사용자 조회, 없으면 예외 발생

        MembersInfoEntity membersInfo = Optional.ofNullable(membersInfoRepository.findByMembersEntity(members))
                .orElseThrow(() -> new AppException(ErrorCode.HISTORY_NOT_FOUND)); // 사용자 상세정보 조회, 없으면 예외 발생

        return historiesRepository.findByMembersInfoEntity(membersInfo)
                .stream()
                .map(historiesEntity -> {   //각 Histories entity에 다음을 수행
                    Orders ordersEntity = ordersRepository.findById(historiesEntity.getOrdersId())
                            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND)); // ordersId로 해당 Orders 조회, 없으면 예외 발생

                    if (historiesEntity.getOrderType() == OrderType.BUY) {  //'구매' 인 경우
                        HistoriesImgEntity historiesImg = Optional.ofNullable(historiesImgRepository.findByHistoriesEntity(historiesEntity))
                                .orElseThrow(() -> new AppException(ErrorCode.IMG_NOT_FOUND)); // 이미지 조회, 없으면 예외 발생

                        return ResponseOrderHistoryDto.builder()
                                .productName(historiesEntity.getName())
                                .productImgUrl(historiesImg.getUrl())
                                .productDescription(historiesEntity.getInfo())
                                .category(historiesEntity.getCategory())
                                .ordersId(ordersEntity.getOrdersId())
                                .createdTime(ordersEntity.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                                .status(ordersEntity.getStatus())
                                .price(historiesEntity.getPrice())
                                .build();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    //구매 상품 상세 조회
    @Override
    public ResponseOrderDetailsDto getOrderDetail(RequestOrderDetailsDto requestOrderDetailsDto) {

        //HistoriesEntity에서 주문 기본 정보를 가져옵니다. (상품 이름, 상품 간단설명, 분류, 주문금액)
        HistoriesEntity histories = historiesRepository.findByOrdersId(requestOrderDetailsDto.getOrdersId());
        if (histories == null) {
            throw new AppException(ErrorCode.HISTORY_NOT_FOUND);
        }

        if (histories.getCategory() == Category.AUC) {  //경매일 때
            return getAuctionOrderDetail(requestOrderDetailsDto.getOrdersId(), requestOrderDetailsDto.getEmail());
        } else {    //비경매일 때
            return getNonAuctionOrderDetail(requestOrderDetailsDto.getOrdersId(), requestOrderDetailsDto.getEmail());
        }
    }

    public ResponseOrderDetailsDto getAuctionOrderDetail(Long ordersId, String email) {
        // 구현 로직 ...
        // 경매 관련 정보를 포함한 ResponseOrderDetailsDto를 반환합니다.

        //OrdersEntity에서 주문 상세 정보를 가져옵니다. (주문일자, 주문번호(ordersId), 주문상태)
        Orders orders = ordersRepository.findById(ordersId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        //HistoriesEntity에서 주문 기본 정보를 가져옵니다. (상품 이름, 상품 간단설명, 분류, 주문금액)
        HistoriesEntity histories = historiesRepository.findByOrdersId(ordersId);
        if (histories == null) {
            throw new AppException(ErrorCode.HISTORY_NOT_FOUND);
        }

        //HistoriesImgEntity에서 상품 이미지 URL을 가져옵니다. (상품 사진)
        HistoriesImgEntity historiesImg = historiesImgRepository.findByHistoriesEntity(histories);
        if (historiesImg == null) {
            throw new AppException(ErrorCode.HISTORY_IMG_NOT_FOUND);
        }

        //AuctionEndDatesEntity에서 경매 마감일을 가져옵니다 (경매 상품의 경우). (마감일자)
        AuctionEndDatesEntity auctionEndDates = auctionEndDatesRepository.findByProductsId(orders.getProductsId());
        if (auctionEndDates == null) {
            throw new AppException(ErrorCode.END_NOT_FOUND);
        }

        //Deliveries에서 배송지 정보를 가져옵니다.(배송지명, 받는사람, 주소(우편번호, 상세주소), 연락처)
        Deliveries deliveries = deliveriesRepository.findByOrdersOrdersId(ordersId);
        if (deliveries == null) {
            throw new AppException(ErrorCode.DELIVERY_NOT_FOUND);
        }

        List<Bids> bidsList = bidsRepository.findByProductsIdAndAndEmail(orders.getProductsId(), email);

        // Build AddressInfo
        ResponseOrderDetailsDto.AddressInfo addressInfo = ResponseOrderDetailsDto.AddressInfo.builder()
                .addrName(deliveries.getAddrName())
                .recipient(deliveries.getName())
                .zipCode(deliveries.getZipNum())
                .address(deliveries.getAddr())
                .addressDetail(deliveries.getAddrDetail())
                .contactNumber(deliveries.getTel())
                .build();

        // Build BidDetails
        List<ResponseOrderDetailsDto.BidDetails> bidDetails = bidsList.stream()
                .map(bid -> ResponseOrderDetailsDto.BidDetails.builder()
                        .bidStatus(bid.getStatus())
                        .bidTime(bid.getCreatedDate())
                        .build())
                .collect(Collectors.toList());

        // Build and return the ResponseOrderDetailsDto
        return ResponseOrderDetailsDto.builder()
                .productName(histories.getName())
                .productDescription(histories.getInfo())
                .productImgUrl(historiesImg.getUrl())
                .category(histories.getCategory())
                .ordersId(ordersId)
                .orderDate(auctionEndDates.getEndDate().toString())
                .status(orders.getStatus())
                .price(histories.getPrice())
                .addressInfo(addressInfo)
                .bidDetails(bidDetails)
                .build();
    }

    public ResponseOrderDetailsDto getNonAuctionOrderDetail(Long ordersId, String email) {
        // 구현 로직 ...
        // 비경매 관련 정보만 포함한 ResponseOrderDetailsDto를 반환합니다.
        //OrdersEntity에서 주문 상세 정보를 가져옵니다. (주문일자, 주문번호(ordersId), 주문상태)
        Orders orders = ordersRepository.findById(ordersId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        //HistoriesEntity에서 주문 기본 정보를 가져옵니다. (상품 이름, 상품 간단설명, 분류, 주문금액)
        HistoriesEntity histories = historiesRepository.findByOrdersId(ordersId);
        if (histories == null) {
            throw new AppException(ErrorCode.HISTORY_NOT_FOUND);
        }

        //HistoriesImgEntity에서 상품 이미지 URL을 가져옵니다. (상품 사진)
        HistoriesImgEntity historiesImg = historiesImgRepository.findByHistoriesEntity(histories);
        if (historiesImg == null) {
            throw new AppException(ErrorCode.HISTORY_IMG_NOT_FOUND);
        }

        //Deliveries에서 배송지 정보를 가져옵니다.(배송지명, 받는사람, 주소(우편번호, 상세주소), 연락처)
        Deliveries deliveries = deliveriesRepository.findByOrdersOrdersId(ordersId);
        if (deliveries == null) {
            throw new AppException(ErrorCode.DELIVERY_NOT_FOUND);
        }

        // Build AddressInfo
        ResponseOrderDetailsDto.AddressInfo addressInfo = ResponseOrderDetailsDto.AddressInfo.builder()
                .addrName(deliveries.getAddrName())
                .recipient(deliveries.getName())
                .zipCode(deliveries.getZipNum())
                .address(deliveries.getAddr())
                .addressDetail(deliveries.getAddrDetail())
                .contactNumber(deliveries.getTel())
                .build();

        // Build and return the ResponseOrderDetailsDto without bid details
        return ResponseOrderDetailsDto.builder()
                .productName(histories.getName())
                .productDescription(histories.getInfo())
                .productImgUrl(historiesImg.getUrl())
                .category(histories.getCategory())
                .ordersId(ordersId)
                .orderDate(orders.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .status(orders.getStatus())
                .price(histories.getPrice())
                .addressInfo(addressInfo)
                .build();
    }

    // 판매 내역 조회
    @Override
    public List<ResponseSellHistoryDto> getSellInfo(String email) {
//        MembersEntity members = membersRepository.findByEmail(email)
//                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND)); // 사용자 조회, 없으면 예외 발생

        //'판매' 에 해당하는 histories 조회
        List<HistoriesEntity> sellHistories = historiesRepository.findByMembersInfoEntity_MembersEntity_EmailAndAndOrderType(email, OrderType.SELL);

        return sellHistories.stream()
                .map(this::buildResponseSellHistoryDto)
                .filter(Objects::nonNull) // Filter out nulls if any
                .collect(Collectors.toList());
    }

    private ResponseSellHistoryDto buildResponseSellHistoryDto(HistoriesEntity history) {
        Orders orders = ordersRepository.findById(history.getOrdersId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // '경매' 이고 '낙찰' 일 때 또는 '판매' 이고 '주문완료' 일 때
        if ((history.getCategory() == Category.AUC && orders.getStatus() == OrderStatus.WINNING_BID) ||
                (history.getCategory() == Category.NOR && orders.getStatus() == OrderStatus.ORDER_COMPLETED)) {
            //TODO: productsRepository에 메서드 추가?
            ProductsEntity product = productsRepository.findById(orders.getProductsId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            //HistoriesImgEntity에서 상품 이미지 URL을 가져옵니다. (상품 사진)
            HistoriesImgEntity historyImg = historiesImgRepository.findByHistoriesEntity(history);
            if (historyImg == null) {
                throw new AppException(ErrorCode.HISTORY_IMG_NOT_FOUND);
            }

            //등록 날짜, 판매 날짜
            String createdDate = product.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String soldDate = orders.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            return ResponseSellHistoryDto.builder()
                    .productName(history.getName())
                    .productDescription(history.getInfo())
                    .productImgUrl(historyImg.getUrl())
                    .category(history.getCategory())
                    .createdDate(createdDate)
                    .soldDate(soldDate)
                    .ordersId(orders.getOrdersId())
                    .status(orders.getStatus())
                    .price(history.getPrice())
                    .build();
        }
        return null; // Return null if not meeting the criteria
    }


    //배송지 조회
    @Override
    public List<ResponseAddressDto> getAddressInfo(String email) {
        MembersEntity member = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND)); // 사용자 조회, 없으면 예외 발생

        MembersInfoEntity membersInfo = member.getMembersInfoEntity();
        List<AddressesEntity> addresses = addressesRepository.findAllByMembersInfoEntity(membersInfo);

        return addresses.stream()
                .map(address -> ResponseAddressDto.builder()
                        .addrName(address.getAddrName())
                        .name(address.getName())
                        .zipNum(address.getZipNum())
                        .addr(address.getAddr())
                        .addrDetail(address.getAddrDetail())
                        .tel(address.getTel())
                        .build())
                .collect(Collectors.toList());
    }
}
