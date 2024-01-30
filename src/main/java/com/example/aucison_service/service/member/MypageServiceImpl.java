package com.example.aucison_service.service.member;


import com.example.aucison_service.dto.mypage.*;
import com.example.aucison_service.enums.Category;
import com.example.aucison_service.enums.OStatusEnum;
import com.example.aucison_service.enums.OrderType;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.member.entity.*;
import com.example.aucison_service.jpa.member.repository.*;
import com.example.aucison_service.jpa.product.entity.ProductsEntity;
import com.example.aucison_service.jpa.product.repository.ProductsRepository;
import com.example.aucison_service.jpa.shipping.entity.AuctionEndDatesEntity;
import com.example.aucison_service.jpa.shipping.entity.Bids;
import com.example.aucison_service.jpa.shipping.entity.Deliveries;
import com.example.aucison_service.jpa.shipping.entity.Orders;
import com.example.aucison_service.jpa.shipping.repository.AuctionEndDatesRepository;
import com.example.aucison_service.jpa.shipping.repository.BidsRepository;
import com.example.aucison_service.jpa.shipping.repository.DeliveriesRepository;
import com.example.aucison_service.jpa.shipping.repository.OrdersRepository;
import com.example.aucison_service.service.s3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final MembersImgRepository membersImgRepository;
    private final S3Service s3Service;

    @Autowired
    public MypageServiceImpl(HistoriesRepository historiesRepository, HistoriesImgRepository historiesImgRepository,
                             MembersInfoRepository membersInfoRepository, MembersRepository membersRepository,
                             OrdersRepository ordersRepository, AuctionEndDatesRepository auctionEndDatesRepository,
                             DeliveriesRepository deliveriesRepository, BidsRepository bidsRepository,
                             ProductsRepository productsRepository, AddressesRepository addressesRepository,
                             MembersImgRepository membersImgRepository, S3Service s3Service) {
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
        this.membersImgRepository = membersImgRepository;
        this.s3Service = s3Service;
    }

    //orElseThrow는 entity에 직접 적용할 수 없고, Optional 객체에 사용되어야 한다.
    @Override
    @Transactional(readOnly = true)
    public List<ResponseOrderHistoryDto> getOrderInfo(MemberDetails principal) {
        String email = principal.getMember().getEmail();
        MembersEntity members = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND)); // 사용자 조회, 없으면 예외 발생

        MembersInfoEntity membersInfo = Optional.ofNullable(membersInfoRepository.findByMembersEntity(members))
                .orElseThrow(() -> new AppException(ErrorCode.MEMBERS_INFO_NOT_FOUND)); // 사용자 상세정보 조회, 없으면 예외 발생

        return historiesRepository.findByMembersInfoEntity(membersInfo)
                .stream()
                .map(historiesEntity -> {   //각 Histories entity에 다음을 수행
                    Orders ordersEntity = ordersRepository.findById(historiesEntity.getOrdersId())
                            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND)); // ordersId로 해당 Orders 조회, 없으면 예외 발생

                    if (historiesEntity.getOrderType() == OrderType.BUY) {  //'구매' 인 경우
                        HistoriesImgEntity historiesImg = Optional.ofNullable(historiesImgRepository.findByHistoriesEntity(historiesEntity))
                                .orElseThrow(() -> new AppException(ErrorCode.IMG_NOT_FOUND)); // 이미지 조회, 없으면 예외 발생

                        ProductsEntity product = Optional.ofNullable(productsRepository.findByProductsId(historiesEntity.getProductsId()))
                                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

                        return ResponseOrderHistoryDto.builder()
                                .historiesId(historiesEntity.getId())
                                .productName(product.getName())
                                .productImgUrl(historiesImg.getUrl())
                                .category(product.getCategory())
                                .kind(product.getKind())
                                .ordersId(ordersEntity.getOrdersId())
                                .createdTime(ordersEntity.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                                .oStatus(ordersEntity.getOStatus())
                                .price(ordersEntity.getPayments().getCost())
                                .build();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    //구매 상품 상세 조회
    @Override
    @Transactional(readOnly = true)
    public ResponseOrderDetailsDto getOrderDetail(MemberDetails principal, Long ordersId, Long historiesId) {

        String email = principal.getMember().getEmail();

        HistoriesEntity history = Optional.ofNullable(historiesRepository.findByOrdersId(ordersId))
                .orElseThrow(() -> new AppException((ErrorCode.HISTORY_NOT_FOUND)));

        ProductsEntity product = Optional.ofNullable(productsRepository.findByProductsId(history.getProductsId()))
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (history == null) {
            throw new AppException(ErrorCode.HISTORY_NOT_FOUND);
        }

        if (product.getCategory().equals("AUCS")) {  //경매일 때
            return getAuctionOrderDetail(ordersId, email);
        } else {    //비경매일 때
            return getNonAuctionOrderDetail(ordersId, email);
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

        ProductsEntity product = productsRepository.findByProductsId(histories.getProductsId());
        if (product == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

//        //AuctionEndDatesEntity에서 경매 마감일을 가져옵니다 (경매 상품의 경우). (마감일자)
//        AuctionEndDatesEntity auctionEndDates = auctionEndDatesRepository.findByProductsId(orders.getProductsId());
//        if (auctionEndDates == null) {
//            throw new AppException(ErrorCode.END_NOT_FOUND);
//        }

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
                        .bidStatus(bid.getOStatus())
                        .bidTime(bid.getCreatedDate())
                        .build())
                .collect(Collectors.toList());

        // Build and return the ResponseOrderDetailsDto
        return ResponseOrderDetailsDto.builder()
                .productName(product.getName())
                .productImgUrl(historiesImg.getUrl())
                .category(product.getCategory())
                .kind(product.getKind())
                .ordersId(ordersId)
                .orderDate(orders.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .endDate(product.getAucsInfosEntity().getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .oStatus(orders.getOStatus())
                .price(orders.getPayments().getCost())
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

        ProductsEntity product = productsRepository.findByProductsId(histories.getProductsId());
        if (product == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
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
                .productName(product.getName())
                .productImgUrl(historiesImg.getUrl())
                .category(product.getCategory())
                .kind(product.getKind())
                .ordersId(ordersId)
                .orderDate(orders.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .oStatus(orders.getOStatus())
                .price(product.getSaleInfosEntity().getPrice())
                .addressInfo(addressInfo)
                .build();
    }

    // 판매 내역 조회
    @Override
    @Transactional(readOnly = true)
    public List<ResponseSellHistoryDto> getSellInfo(MemberDetails principal) {
//        MembersEntity members = membersRepository.findByEmail(email)
//                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND)); // 사용자 조회, 없으면 예외 발생
        String email = principal.getMember().getEmail();

        //'판매' 에 해당하는 histories 조회
        List<HistoriesEntity> sellHistories = historiesRepository.findByMembersInfoEntity_MembersEntity_EmailAndAndOrderType(email, OrderType.SELL);

        return sellHistories.stream()
                .map(this::buildResponseSellHistoryDto)
                .filter(Objects::nonNull) // Filter out nulls if any
                .collect(Collectors.toList());
    }

    private ResponseSellHistoryDto buildResponseSellHistoryDto(HistoriesEntity history) {

        //HistoriesImgEntity에서 상품 이미지 URL을 가져옵니다. (상품 사진)
        HistoriesImgEntity historyImg = historiesImgRepository.findByHistoriesEntity(history);
        if (historyImg == null) {
            throw new AppException(ErrorCode.HISTORY_IMG_NOT_FOUND);
        }

        ProductsEntity product = productsRepository.findByProductsId(history.getProductsId());

        String soldDate = null;
        if (history.getSoldDate() == null) {
            soldDate = "";
        } else {
            soldDate = history.getSoldDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        Float price = null;
        if (product.getCategory().equals("AUCS")) {
            price = product.getAucsInfosEntity().getStartPrice();   //현재 최고가 or 시작가??
        } else {
            price = product.getSaleInfosEntity().getPrice();
        }

        return ResponseSellHistoryDto.builder()
                .productName(product.getName())
                .productImgUrl(historyImg.getUrl())
                .category(product.getCategory())
                .kind(product.getKind())
                .createdDate(history.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .soldDate(soldDate)
                .pStatus(product.getPStatus())
                .price(price)
                .build();
    }


    //회원 정보 조회
    @Override
    @Transactional(readOnly = true)
    public ResponseMemberProfileDto getMemberProfile(MemberDetails principal) {
        String email = principal.getMember().getEmail();
        MembersEntity member = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        MembersInfoEntity membersInfo = member.getMembersInfoEntity();

        MembersImgEntity membersImg = membersImgRepository.findByMembersInfoEntity(membersInfo);

        return ResponseMemberProfileDto.builder()
                .profileUrl(membersImg.getUrl())
                .nickname(member.getNickname())
                .email(membersInfo.getSubEmail())
                .phone(membersInfo.getPhone())
                .build();
    }

    @Override
    @Transactional
    public void patchMemberDetails(MemberDetails principal, RequestMembersInfoDto requestMembersInfoDto) {
        String email = principal.getMember().getEmail();

        MembersEntity member = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        MembersInfoEntity memberInfo = Optional.ofNullable(membersInfoRepository.findByMembersEntity(member))
                .orElseThrow(() -> new AppException(ErrorCode.MEMBERS_INFO_NOT_FOUND));

        // Update nickname, phone, and subEmail
        if (requestMembersInfoDto.getNickName() != null) {
            member.updateNickname(requestMembersInfoDto.getNickName());
        }
        if (requestMembersInfoDto.getPhone() != null) {
            memberInfo.updatePhone(requestMembersInfoDto.getPhone());
        }
        if (requestMembersInfoDto.getSubEmail() != null) {
            memberInfo.updateSubEmail(requestMembersInfoDto.getSubEmail());
        }


        // 이미지 처리
        if (requestMembersInfoDto.getImgUrl() != null && !requestMembersInfoDto.getImgUrl().isEmpty()) {
            String folderName = "membersProfile"; // 폴더 이름 정의
            String imgUrl = s3Service.uploadFileAndGetUrl(requestMembersInfoDto.getImgUrl(), folderName);

            MembersImgEntity membersImg = memberInfo.getMembersImgEntity();
            if (membersImg == null) {
                membersImg = MembersImgEntity.builder()
                        .url(imgUrl)
                        .membersInfoEntity(memberInfo)
                        .build();
            } else {
                // 기존 이미지 삭제
                s3Service.deleteFileFromS3Bucket(membersImg.getUrl(), folderName);
            }
            membersImg.updateInfo(memberInfo, imgUrl);
            membersImgRepository.save(membersImg);
        }


    }

}
