//package com.example.aucison_service.service.member;
//
//
//import com.example.aucison_service.dto.mypage.RequestOrderDetailsDto;
//import com.example.aucison_service.dto.mypage.ResponseOrderDetailsDto;
//import com.example.aucison_service.dto.mypage.ResponseOrderHistoryDto;
//import com.example.aucison_service.dto.mypage.ResponseSellHistoryDto;
//
//import java.util.List;
//
//public interface MypageService {
//    // 기본 구매 내역 조회
//    List<ResponseOrderHistoryDto> getOrderInfo(String email);
//    // 기본 구매 내역 조회 -> 상세 조회
//    ResponseOrderDetailsDto getOrderDetail(RequestOrderDetailsDto requestOrderDetailsDto) throws Exception;
//    // 판매 내역 조회
//    List<ResponseSellHistoryDto> getSellInfo(String email);
//    // 회원 정보 조회
//    //MemberInfoDto getMemberDetails(Long membersId) throws Exception;
//    // 회원 정보 수정
//    //void patchMemberDetails(RequestMembersInfoDto requestMembersInfoDto);
//}
