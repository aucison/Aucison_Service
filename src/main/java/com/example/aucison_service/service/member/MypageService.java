package com.example.aucison_service.service.member;


import com.example.aucison_service.dto.mypage.*;

import java.util.List;

public interface MypageService {
    // 기본 구매 내역 조회
    List<ResponseOrderHistoryDto> getOrderInfo(String email);
    // 기본 구매 내역 조회 -> 상세 조회
    ResponseOrderDetailsDto getOrderDetail(RequestOrderDetailsDto requestOrderDetailsDto) throws Exception;
    // 판매 내역 조회
    List<ResponseSellHistoryDto> getSellInfo(String email);
    // 배송지 조회
    List<ResponseAddressDto> getAddressInfo(String email);
    // 배송지 등록
    void addAddress(String email, RequestAddressDto requestAddressDto);
    // 배송지 삭제
    void deleteAddress(String email, String addrName);
    // 배송지 수정
    // 회원 정보 조회
    //MemberInfoDto getMemberDetails(Long membersId) throws Exception;
    // 회원 정보 수정
    //void patchMemberDetails(RequestMembersInfoDto requestMembersInfoDto);
}
