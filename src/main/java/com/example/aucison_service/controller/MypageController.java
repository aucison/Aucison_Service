package com.example.aucison_service.controller;


import com.example.aucison_service.dto.ApiResponse;
import com.example.aucison_service.dto.mypage.RequestAddressDto;
import com.example.aucison_service.dto.mypage.RequestMembersInfoDto;
import com.example.aucison_service.dto.mypage.RequestUpdateAddressDto;
import com.example.aucison_service.service.address.AddressService;
import com.example.aucison_service.service.member.MemberDetails;
import com.example.aucison_service.service.member.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mp")
public class MypageController {

    private final MypageService mypageService;
    private final AddressService addressService;

    @GetMapping("/buy") //구매 내역 조회
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<?> getOrderInfo(@AuthenticationPrincipal MemberDetails principal) {
        // principal에서 이메일 가져오기
        return ApiResponse.createSuccess(mypageService.getOrderInfo(principal));
    }

@GetMapping("/buy/{historiesId}")   //구매 내역 상세 조회
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<?> getOrderDetail(@PathVariable("historiesId") Long historiesId,
                                         @AuthenticationPrincipal MemberDetails principal) {
//        RequestOrderDetailsDto requestDto = RequestOrderDetailsDto.builder()
//                .email(email)
//                .ordersId(ordersId)
//                .historiesId(historiesId)
//                .build();

        return ApiResponse.createSuccess(mypageService.getOrderDetail(principal, historiesId));
    }

@GetMapping("/sell")    //판매 내역 조회
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<?> getSellInfo(@AuthenticationPrincipal MemberDetails principal) {
//        String email = principal.getAttribute("email");
        return ApiResponse.createSuccess(mypageService.getSellInfo(principal));
    }

//    @GetMapping("/address") // 회원 정보 조회(배송지 조회)
//    @PreAuthorize("isAuthenticated()")
//    public ApiResponse<?> getAddressInfo(@AuthenticationPrincipal MemberDetails principal) {
////        String email = principal.getAttribute("email");
//        return ApiResponse.createSuccess(mypageService.getAddressInfo(principal));
//    }

//    @GetMapping("/address") // 회원 정보 조회(배송지 조회)
//    @PreAuthorize("isAuthenticated()")
//    public ApiResponse<?> getAddressInfo(@AuthenticationPrincipal MemberDetails principal) {
////        String email = principal.getAttribute("email");
//        return ApiResponse.createSuccess(mypageService.getAddressInfo(principal));
//    }

    // 배송지 등록
    /*
    이 코드 기준으로 코드 작성
     */
//    @PostMapping("/address")
//    @PreAuthorize("isAuthenticated()")
//    public ApiResponse<?> addAddress(@AuthenticationPrincipal MemberDetails principal,
//                                     @RequestBody RequestAddressDto requestAddressDto) {
////        MembersEntity members = principal.getMember();
//        addressService.addAddress(principal, requestAddressDto);
//        return ApiResponse.createSuccessWithNoData("배송지 등록 성공");
//    }

//    // 배송지 삭제
//    @DeleteMapping("/address")
//    @PreAuthorize("isAuthenticated()")
//    public ApiResponse<?> deleteAddress(@AuthenticationPrincipal MemberDetails principal,
//                                        @RequestParam String addrName) {
////        String email = principal.getAttribute("email");
//        mypageService.deleteAddress(principal, addrName);
//        return ApiResponse.createSuccessWithNoData("배송지 삭제 성공");
//    }

//    //배송지 수정
//    @PatchMapping("/address") // 배송지 수정
//    @PreAuthorize("isAuthenticated()")
//    public ApiResponse<?> updateAddress(@AuthenticationPrincipal MemberDetails principal,
//                                        @RequestParam String addrName,
//                                        @RequestBody RequestUpdateAddressDto requestUpdateAddressDto) {
////        String email = principal.getAttribute("email");
//        mypageService.updateAddressByEmailAndAddrName(principal, addrName, requestUpdateAddressDto);
//        return ApiResponse.createSuccessWithNoData("배송지 수정 성공");
//    }

    // 택배 팝업

    // 회원 정보 조회
    @GetMapping("/profile") // 회원 정보 조회
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<?> getMemberProfile(@AuthenticationPrincipal MemberDetails principal) {
//        String email = principal.getAttribute("email");
        //로그 확인용
        if (principal == null || principal.getMember() == null) {
            throw new IllegalArgumentException("사용자 인증 정보가 유효하지 않습니다.");
        }
        //
        return ApiResponse.createSuccess(mypageService.getMemberProfile(principal));
    }

    // 회원 정보 수정
    @PatchMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<?> patchMemberDetails(@AuthenticationPrincipal MemberDetails principal,
                                             @ModelAttribute RequestMembersInfoDto requestMembersInfoDto) {
        mypageService.patchMemberDetails(principal, requestMembersInfoDto);
        return ApiResponse.createSuccessWithNoData("회원정보 수정 성공");
    }
}
