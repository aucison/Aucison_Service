package com.example.aucison_service.service.member;


import com.example.aucison_service.dto.mypage.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

public interface MypageService {
    // 기본 구매 내역 조회
    List<ResponseOrderHistoryDto> getOrderInfo(MemberDetails principal);
    // 기본 구매 내역 조회 -> 상세 조회
    ResponseOrderDetailsDto getOrderDetail(MemberDetails principal, Long ordersId, Long historiesId);
    // 판매 내역 조회
    List<ResponseSellHistoryDto> getSellInfo(MemberDetails principal);
    // 배송지 조회
    List<ResponseAddressDto> getAddressInfo(MemberDetails principal);
    // 배송지 등록
    void addAddress(MemberDetails principal, RequestAddressDto requestAddressDto);
    // 배송지 삭제
    void deleteAddress(MemberDetails principal, String addrName);
    // 배송지 수정
    void updateAddressByEmailAndAddrName(MemberDetails principal, String addrName, RequestUpdateAddressDto requestUpdateAddressDto);
    // 회원 정보 조회
    ResponseMemberProfileDto getMemberProfile(MemberDetails principal);
    // 회원 정보 수정
    void patchMemberDetails(MemberDetails principal, RequestMembersInfoDto requestMembersInfoDto);
}
