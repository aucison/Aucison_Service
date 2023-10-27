package com.example.aucison_service.service.member;


import com.example.aucison_service.dto.mypage.RequestOrderDetailsDto;
import com.example.aucison_service.dto.mypage.ResponseOrderDetailsDto;
import com.example.aucison_service.dto.mypage.ResponseOrderHistoryDto;
import com.example.aucison_service.dto.mypage.ResponseSellHistoryDto;
import com.example.aucison_service.enums.OrderStatus;
import com.example.aucison_service.jpa.member.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {

    private final HistoriesRepository historiesRepository;
    private final MembersInfoRepository membersInfoRepository;
    private final MembersImgRepository membersImgRepository;
    private final MembersRepository membersRepository;

    @Override
    public List<ResponseOrderHistoryDto> getOrderHistoryList(String email) {
        MembersEntity membersEntity = membersRepository.findByEmail(email);
        return historiesRepository.findByMembersInfo(membersInfoRepository.findByMembers(membersEntity))
                .stream()
                .filter(historiesEntity -> historiesEntity.getOrderStatus() == OrderStatus.BUY)
                .map(historiesEntity -> {
                    ResponseOrderHistoryDto responseOrderHistoryDto = new ResponseOrderHistoryDto(historiesEntity);
                    responseOrderHistoryDto.setImgUrl(membersImgRepository.findByMembersInfo
                            (membersInfoRepository.findByMembers(membersEntity)).getUrl());
                    //responseOrderHistoryDto.setOrdersAt("product-server에서 받아오기");
                    //responseOrderHistoryDto.setState("product-server에서 받아오기");
                    return responseOrderHistoryDto;
                }).collect(Collectors.toList());
    }

    @Override
    public ResponseOrderDetailsDto getOrderDetails(RequestOrderDetailsDto requestOrderDetailsDto) throws Exception {
        MembersEntity membersEntity = membersRepository.findByEmail(requestOrderDetailsDto.getEmail());
        ResponseOrderDetailsDto responseOrderDetailsDto = new ResponseOrderDetailsDto(historiesRepository.findById
                (requestOrderDetailsDto.getHistoriesId()).orElseThrow(() -> new Exception("수정필요수정필요")));
        responseOrderDetailsDto.setImgUrl(membersImgRepository.findByMembersInfo(membersInfoRepository.findByMembers
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
    public List<ResponseSellHistoryDto> getSellHistoryList(String email) {
        MembersEntity membersEntity = membersRepository.findByEmail(email);
        return historiesRepository.findByMembersInfo(membersInfoRepository.findByMembers(membersEntity))
                .stream()
                .filter(historiesEntity -> historiesEntity.getOrderStatus() == OrderStatus.SELL)
                .map(historiesEntity -> {
                    ResponseSellHistoryDto responseSellHistoryDto = new ResponseSellHistoryDto(historiesEntity);
                    responseSellHistoryDto.setImgUrl(membersImgRepository.findByMembersInfo
                            (membersInfoRepository.findByMembers(membersEntity)).getUrl());
                    //responseSellHistoryDto.setState("product-server에서 받아오기");
                    return responseSellHistoryDto;
                }).collect(Collectors.toList());
    }
}
