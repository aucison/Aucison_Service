package com.example.aucison_service.controller;


import com.example.aucison_service.dto.ApiResponse;
import com.example.aucison_service.dto.mypage.RequestAddressDto;
import com.example.aucison_service.dto.mypage.RequestOrderDetailsDto;
import com.example.aucison_service.service.member.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mp")
public class MypageController {

    private final MypageService mypageService;

    @GetMapping("/buy") //구매 내역 조회
    public ApiResponse<?> getOrderInfo(@AuthenticationPrincipal OAuth2User principal) {
        // principal에서 이메일 가져오기
        String email = principal.getAttribute("email");
        return ApiResponse.createSuccess(mypageService.getOrderInfo(email));
    }

    @GetMapping("/buy/{historiesId}")   //구매 내역 상세 조회
    public ApiResponse<?> getOrderDetail(@PathVariable("historiesId") Long historiesId,
                                            @RequestParam Long ordersId,
                                            @AuthenticationPrincipal OAuth2User principal) throws Exception {
        String email = principal.getAttribute("email");
        RequestOrderDetailsDto requestDto = RequestOrderDetailsDto.builder()
                .email(email)
                .ordersId(ordersId)
                .historiesId(historiesId)
                .build();

        return ApiResponse.createSuccess(mypageService.getOrderDetail(requestDto));
    }

    @GetMapping("/sell")    //판매 내역 조회
    public ApiResponse<?> getSellInfo(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        return ApiResponse.createSuccess(mypageService.getSellInfo(email));
    }

    @GetMapping("/address") // 회원 정보 조회(배송지 조회)
    public ApiResponse<?> getAddressInfo(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        return ApiResponse.createSuccess(mypageService.getAddressInfo(email));
    }

    // 배송지 등록
    @PostMapping("/address") // 배송지 등록
    public ApiResponse<?> addAddress(@AuthenticationPrincipal OAuth2User principal,
                                     @RequestBody RequestAddressDto requestAddressDto) {
        String email = principal.getAttribute("email");
        mypageService.addAddress(email, requestAddressDto);
        return ApiResponse.createSuccessWithNoData("배송지 등록 성공");
    }
    // 배송지 삭제
    // 배송지 수정
    // 택배 팝업
    // 회원 정보 조회
    // 회원 정보 수정
}
