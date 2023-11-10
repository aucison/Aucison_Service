package com.example.aucison_service.service.member;


import com.example.aucison_service.dto.mypage.RequestOrderDetailsDto;
import com.example.aucison_service.dto.mypage.ResponseOrderDetailsDto;
import com.example.aucison_service.dto.mypage.ResponseOrderHistoryDto;
import com.example.aucison_service.dto.mypage.ResponseSellHistoryDto;
import com.example.aucison_service.enums.OrderStatus;
import com.example.aucison_service.enums.OrderType;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.member.*;
import com.example.aucison_service.jpa.shipping.Orders;
import com.example.aucison_service.jpa.shipping.OrdersRepository;
import lombok.RequiredArgsConstructor;
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

    @Autowired
    public MypageServiceImpl(HistoriesRepository historiesRepository, HistoriesImgRepository historiesImgRepository,
                             MembersInfoRepository membersInfoRepository, MembersRepository membersRepository,
                             OrdersRepository ordersRepository) {
        this.historiesRepository = historiesRepository;
        this.historiesImgRepository = historiesImgRepository;
        this.membersInfoRepository = membersInfoRepository;
        this.membersRepository = membersRepository;
        this.ordersRepository = ordersRepository;
    }

    //orElseThrow는 entity에 직접 적용할 수 없고, Optional 객체에 사용되어야 한다.
    @Override
    public List<ResponseOrderHistoryDto> getOrderInfo(String email) {
        MembersEntity members = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND)); // 사용자 조회, 없으면 예외 발생

        MembersInfoEntity membersInfo = Optional.ofNullable(membersInfoRepository.findByMembersEntity(members))
                .orElseThrow(() -> new AppException(ErrorCode.HISTORY_NOT_FOUND)); // 사용자 상세정보 조회, 없으면 예외 발생

        return historiesRepository.findByMembersInfo(membersInfo)
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


    @Override
    public ResponseOrderDetailsDto getOrderDetail(RequestOrderDetailsDto requestOrderDetailsDto) throws Exception {
        MembersEntity membersEntity = membersRepository.findByEmail(requestOrderDetailsDto.getEmail());
        ResponseOrderDetailsDto responseOrderDetailsDto = new ResponseOrderDetailsDto(historiesRepository.findById
                (requestOrderDetailsDto.getHistoriesId()).orElseThrow(() -> new Exception("수정필요수정필요")));
        responseOrderDetailsDto.setImgUrl(membersImgRepository.findByMembersInfo(membersInfoRepository.findByMembersEntity
                (membersEntity)).getUrl());
        //responseOrderDetailsDto.setOrdersAt("product-server에서 받아오기");
        //responseOrderDetailsDto.setState("product-server에서 받아오기");
        //responseOrderDetailsDto.setReceiver("shipping-server에서 받아오기");
        //responseOrderDetailsDto.setAddrName("shipping-server에서 받아오기");
        //responseOrderDetailsDto.setAddr("shipping-server에서 받아오기");
        //responseOrderDetailsDto.setAddrDetail("shipping-server에서 받아오기");
        //responseOrderDetailsDto.setTel("shipping-server에서 받아오기");
        //responseOrderDetailsDto.setIsCompleted("shipping-server에서 받아오기");
        //responseOrderDetailsDto.setIsStarted("shipping-server에서 받아오기");
        //responseOrderDetailsDto.setBidsHistory("shipping-server에서 받아오기");

        return responseOrderDetailsDto;
    }

    // 판매 내역 조회
    @Override
    public List<ResponseSellHistoryDto> getSellInfo(String email) {
        MembersEntity membersEntity = membersRepository.findByEmail(email);
        return historiesRepository.findByMembersInfo(membersInfoRepository.findByMembersEntity(membersEntity))
                .stream()
                .filter(historiesEntity -> historiesEntity.getOrderStatus() == OrderStatus.SELL)
                .map(historiesEntity -> {
                    ResponseSellHistoryDto responseSellHistoryDto = new ResponseSellHistoryDto(historiesEntity);
                    responseSellHistoryDto.setImgUrl(membersImgRepository.findByMembersInfo
                            (membersInfoRepository.findByMembersEntity(membersEntity)).getUrl());
                    //responseSellHistoryDto.setState("product-server에서 받아오기");
                    return responseSellHistoryDto;
                }).collect(Collectors.toList());
    }
}
