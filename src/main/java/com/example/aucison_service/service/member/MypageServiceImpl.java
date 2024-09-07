package com.example.aucison_service.service.member;


import com.example.aucison_service.controller.AuthController;
import com.example.aucison_service.dto.mypage.*;
import com.example.aucison_service.enums.OrderType;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.member.entity.*;
import com.example.aucison_service.jpa.member.repository.*;
import com.example.aucison_service.jpa.product.entity.ProductsEntity;
import com.example.aucison_service.jpa.product.repository.ProductsRepository;
import com.example.aucison_service.jpa.shipping.entity.Bids;
import com.example.aucison_service.jpa.shipping.entity.Deliveries;
import com.example.aucison_service.jpa.shipping.entity.Orders;
import com.example.aucison_service.jpa.shipping.repository.BidsRepository;
import com.example.aucison_service.jpa.shipping.repository.DeliveriesRepository;
import com.example.aucison_service.jpa.shipping.repository.OrdersRepository;
import com.example.aucison_service.service.s3.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(MypageServiceImpl.class);//오류 확인을 위한 로그
    private final HistoriesRepository historiesRepository;
    private final HistoriesImgRepository historiesImgRepository;
    private final MembersInfoRepository membersInfoRepository;
    private final MembersRepository membersRepository;
    private final OrdersRepository ordersRepository;
    private  final DeliveriesRepository deliveriesRepository;
    private final BidsRepository bidsRepository;
    private final ProductsRepository productsRepository;
    private final MembersImgRepository membersImgRepository;
    private final WishesRepository wishesRepository;
    private final S3Service s3Service;

    @Autowired
    public MypageServiceImpl(HistoriesRepository historiesRepository, HistoriesImgRepository historiesImgRepository,
                             MembersInfoRepository membersInfoRepository, MembersRepository membersRepository,
                             OrdersRepository ordersRepository,
                             DeliveriesRepository deliveriesRepository, BidsRepository bidsRepository,
                             ProductsRepository productsRepository, MembersImgRepository membersImgRepository,
                             WishesRepository wishesRepository, S3Service s3Service) {
        this.historiesRepository = historiesRepository;
        this.historiesImgRepository = historiesImgRepository;
        this.membersInfoRepository = membersInfoRepository;
        this.membersRepository = membersRepository;
        this.ordersRepository = ordersRepository;
        this.deliveriesRepository = deliveriesRepository;
        this.bidsRepository = bidsRepository;
        this.productsRepository = productsRepository;
        this.membersImgRepository = membersImgRepository;
        this.wishesRepository = wishesRepository;
        this.s3Service = s3Service;
    }

    //orElseThrow는 entity에 직접 적용할 수 없고, Optional 객체에 사용되어야 한다.
    // 구매내역 조회
    @Override
    @Transactional(readOnly = true)
    public List<ResponseOrderHistoryDto> getOrderInfo(MemberDetails principal) {

        MembersInfoEntity membersInfoEntity = membersInfoRepository.findByMembersEntity(principal.getMember());
        // '구매'에 해당하는 histories 조회
        List<HistoriesEntity> buyHistories = historiesRepository.findByMembersInfoEntity_IdAndOrderType(membersInfoEntity.getId(), OrderType.BUY);

        return buyHistories.stream()
                .map(this::buildResponseOrderHistoryDto)
                .filter(Objects::nonNull) // null이 아닌 경우만 필터링
                .collect(Collectors.toList());
    }

    // 구매내역 dto 생성
    private ResponseOrderHistoryDto buildResponseOrderHistoryDto(HistoriesEntity history) {

        String url = null;
        HistoriesImgEntity historiesImg = historiesImgRepository.findByHistoriesEntity(history);
        if (historiesImg == null) {
            url = null;
        } else {
            url = historiesImg.getUrl();
        }

        Float price = null;
        if (history.getCategory().equals("AUCS")) {
            price = history.getHighestPrice();  //본인 입찰가 기준으로 보임
        } else if(history.getCategory().equals("SALE")){
            price = history.getSalePrice();     //일반상품
        }

        return ResponseOrderHistoryDto.builder()
                .historiesId(history.getHistoriesId())
                .productName(history.getName())
                .productImgUrl(url)
                .category(history.getCategory())
                .kind(history.getKind())
                .ordersId(history.getOrdersId())
                .createdTime(history.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .oStatus(history.getOStatus())
                .price(price)
                .build();
    }


    //구매 상품 상세 조회
    @Override
    @Transactional(readOnly = true)
    public ResponseOrderDetailsDto getOrderDetail(MemberDetails principal, Long historiesId) {

        HistoriesEntity historiesEntity = historiesRepository.findByHistoriesId(historiesId);


        if (historiesEntity.getCategory().equals("AUCS")) {  //경매일 때
            return getAucsOrderDetail(principal, historiesEntity);
        } else if(historiesEntity.getCategory().equals("SALE")){    //비경매일 때
            return getSaleOrderDetail(principal, historiesEntity);
        }
        else {
            throw new AppException(ErrorCode.HISTORY_NOT_FOUND);
        }
    }

    //구매 상품 상세 조회 - 경매상품
    public ResponseOrderDetailsDto getAucsOrderDetail(MemberDetails principal, HistoriesEntity historiesEntity) {

        String url = null;
        HistoriesImgEntity historiesImg = historiesEntity.getHistoriesImg();
        if (historiesImg == null) {
            url = null;
        } else {
            url = historiesImg.getUrl();
        }

        Long ordersId = historiesEntity.getOrdersId();

        //Deliveries에서 배송지 정보를 가져옴(배송지명, 받는사람, 주소(우편번호, 상세주소), 연락처)
        Deliveries deliveries = deliveriesRepository.findByOrdersOrdersId(ordersId);
        if (deliveries == null) {
            throw new AppException(ErrorCode.DELIVERY_NOT_FOUND);
        }

        String email = principal.getMember().getEmail();
        List<Bids> bidsList = bidsRepository.findByProductsIdAndAndEmail(historiesEntity.getProductsId(), email);


        ResponseOrderDetailsDto.AddressInfo addressInfo = ResponseOrderDetailsDto.AddressInfo.builder()
                .addrName(deliveries.getAddrName())
                .recipient(deliveries.getName())
                .zipCode(deliveries.getZipNum())
                .address(deliveries.getAddr())
                .addressDetail(deliveries.getAddrDetail())
                .contactNumber(deliveries.getTel())
                .build();


        List<ResponseOrderDetailsDto.BidDetails> bidDetails = bidsList.stream()
                .map(bid -> ResponseOrderDetailsDto.BidDetails.builder()
                        .bidStatus(bid.getOStatus())
                        .bidTime(bid.getCreatedDate())
                        .build())
                .collect(Collectors.toList());


        return ResponseOrderDetailsDto.builder()
                .productName(historiesEntity.getName())
                .productImgUrl(url)
                .category(historiesEntity.getCategory())
                .kind(historiesEntity.getKind())
                .ordersId(historiesEntity.getOrdersId())
                .orderDate(historiesEntity.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .oStatus(historiesEntity.getOStatus())
                .price(historiesEntity.getHighestPrice())
                .addressInfo(addressInfo)
                .bidDetails(bidDetails)
                .build();


    }

    //구매 상품 상세 조회 - 비경매상품
    public ResponseOrderDetailsDto getSaleOrderDetail(MemberDetails principal, HistoriesEntity historiesEntity) {


        String url = null;
        HistoriesImgEntity historiesImg = historiesEntity.getHistoriesImg();
        if (historiesImg == null) {
            url = null;
        } else {
            url = historiesImg.getUrl();
        }

        Long ordersId = historiesEntity.getOrdersId();

        //Deliveries에서 배송지 정보를 가져옴(배송지명, 받는사람, 주소(우편번호, 상세주소), 연락처)
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
                .productName(historiesEntity.getName())
                .productImgUrl(url)
                .category(historiesEntity.getCategory())
                .kind(historiesEntity.getKind())
                .ordersId(historiesEntity.getOrdersId())
                .orderDate(historiesEntity.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .oStatus(historiesEntity.getOStatus())
                .price(historiesEntity.getSalePrice())
                .addressInfo(addressInfo)
                .build();

    }

    // 판매 내역 조회
    @Override
    @Transactional(readOnly = true)
    public List<ResponseSellHistoryDto> getSellInfo(MemberDetails principal) {


        MembersInfoEntity membersInfoEntity = membersInfoRepository.findByMembersEntity(principal.getMember());
        //'판매' 에 해당하는 histories 조회
        List<HistoriesEntity> sellHistories = historiesRepository.findByMembersInfoEntity_IdAndOrderType(membersInfoEntity.getId(), OrderType.SELL);

        return sellHistories.stream()
                .map(this::buildResponseSellHistoryDto)
                .filter(Objects::nonNull) //  null이 아닌 경우만 필터링
                .collect(Collectors.toList());
    }

    //판매내역 dto생성
    private ResponseSellHistoryDto buildResponseSellHistoryDto(HistoriesEntity historyEntity) {

        String url = null;
        HistoriesImgEntity historiesImg = historiesImgRepository.findByHistoriesEntity(historyEntity);
        if (historiesImg == null) {
            url = null;
        } else {
            url = historiesImg.getUrl();
        }

        Float price = null;
        if (historyEntity.getCategory().equals("AUCS")) {
            price = historyEntity.getHighestPrice();  //등록가 기준으로 보임, 최고가 아님
        } else if(historyEntity.getCategory().equals("SALE")){
            price = historyEntity.getSalePrice();     //일반상품
        }

        return ResponseSellHistoryDto.builder()
                .historiesId(historyEntity.getHistoriesId())
                .productName(historyEntity.getName())
                .productImgUrl(url)
                .category(historyEntity.getCategory())
                .kind(historyEntity.getKind())
                .createdDate(historyEntity.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .pStatus(historyEntity.getPStatus())
                .ordersId(historyEntity.getOrdersId())
                .price(price)
                .build();
    }



    //회원 정보 조회
    @Override
    @Transactional(readOnly = true)
    public ResponseMemberProfileDto getMemberProfile(MemberDetails principal) {
        String email = principal.getMember().getEmail();

        MembersEntity member = membersRepository.findByEmail(email);
        if (member == null) {
            throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
        }

        MembersInfoEntity membersInfo = membersInfoRepository.findByMembersEntity(member);
        if (membersInfo == null) {
            throw new AppException(ErrorCode.MEMBERS_INFO_NOT_FOUND);
        }

        String membersImgUrl = null;
        if (membersImgRepository.findByMembersInfoEntity(membersInfo) == null) {
            membersImgUrl = null;
        } else {
            membersImgUrl = membersImgRepository.findByMembersInfoEntity(membersInfo).getUrl();
        }

        //size()를 사용할 경우 모든 데이터를 불러와서 size()를 호출하므로 내역이 많은 경우 성능 저하가 예상된다.
        //따라서 데이터베이스에서 직접 개수를 계산하는 것이 효율적이다.
//        int buyCount = historiesRepository.findByEmailAndOrderType(email, OrderType.BUY).size();
//        int sellCount = historiesRepository.findByEmailAndOrderType(email, OrderType.SELL).size();
//        int wishCount = wishesRepository.findByMembersEntity(member).size();

        // 직접 개수를 계산하는 쿼리 메서드 사용
        int buyCount = historiesRepository.countByOrderTypeAndMembersInfoId(membersInfo.getId(), OrderType.BUY);
        int sellCount = historiesRepository.countByOrderTypeAndMembersInfoId(membersInfo.getId(), OrderType.SELL);
        int wishCount = wishesRepository.countByMember(member);

        return ResponseMemberProfileDto.builder()
                .profileUrl(membersImgUrl)
                .nickname(member.getNickname())
                .email(membersInfo.getSubEmail())
                .phone(membersInfo.getPhone())
                .credit(membersInfo.getCredit())
                .buyCount(buyCount)
                .sellCount(sellCount)
                .wishCount(wishCount)
                .build();
    }

    @Override
    @Transactional
    public void patchMemberDetails(MemberDetails principal, RequestMembersInfoDto requestMembersInfoDto) {
        String email = principal.getMember().getEmail();

        MembersEntity member = membersRepository.findByEmail(email);
        if (member == null) {
            throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
        }

        MembersInfoEntity membersInfo = membersInfoRepository.findByMembersEntity(member);
        if (membersInfo == null) {
            throw new AppException(ErrorCode.MEMBERS_INFO_NOT_FOUND);
        }

        // Update nickname, phone, and subEmail
        if (requestMembersInfoDto.getNickName() != null) {
            member.updateNickname(requestMembersInfoDto.getNickName());
        }
        if (requestMembersInfoDto.getPhone() != null) {
            membersInfo.updatePhone(requestMembersInfoDto.getPhone());
        }
        if (requestMembersInfoDto.getSubEmail() != null) {
            membersInfo.updateSubEmail(requestMembersInfoDto.getSubEmail());
        }


        // 이미지 처리
        if (requestMembersInfoDto.getImgUrl() != null && !requestMembersInfoDto.getImgUrl().isEmpty()) {
            String folderName = "membersProfile"; // 폴더 이름 정의
            String imgUrl = s3Service.uploadFileAndGetUrl(requestMembersInfoDto.getImgUrl(), folderName);

            MembersImgEntity membersImg = membersInfo.getMembersImgEntity();
            if (membersImg == null) {
                membersImg = MembersImgEntity.builder()
                        .url(imgUrl)
                        .membersInfoEntity(membersInfo)
                        .build();
            } else {
                // 기존 이미지 삭제
                s3Service.deleteFileFromS3Bucket(membersImg.getUrl(), folderName);
            }
            membersImg.updateInfo(membersInfo, imgUrl);
            membersImgRepository.save(membersImg);
        }


    }

}
