package com.example.aucison_service.controller;

import com.example.aucison_service.dto.ApiResponse;
import com.example.aucison_service.dto.mypage.RequestAddressDto;
import com.example.aucison_service.dto.mypage.RequestUpdateAddressDto;
import com.example.aucison_service.service.address.AddressService;
import com.example.aucison_service.service.member.MemberDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/address")
public class AddressController {
    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<?> addAddress(@AuthenticationPrincipal MemberDetails principal,
                                     @RequestBody RequestAddressDto requestAddressDto) {
//        MembersEntity members = principal.getMember();
        addressService.addAddress(principal, requestAddressDto);
        return ApiResponse.createSuccessWithNoData("배송지 등록 성공");
    }

    // 배송지 삭제
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<?> deleteAddress(@AuthenticationPrincipal MemberDetails principal,
                                        @RequestParam String addrName) {
//        String email = principal.getAttribute("email");
        addressService.deleteAddress(principal, addrName);
        return ApiResponse.createSuccessWithNoData("배송지 삭제 성공");
    }

    //배송지 수정
    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<?> updateAddress(@AuthenticationPrincipal MemberDetails principal,
                                        @RequestParam String addrName,
                                        @RequestBody RequestUpdateAddressDto requestUpdateAddressDto) {
//        String email = principal.getAttribute("email");
        addressService.updateAddressByEmailAndAddrName(principal, addrName, requestUpdateAddressDto);
        return ApiResponse.createSuccessWithNoData("배송지 수정 성공");
    }


    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<?> getAddressInfo(@AuthenticationPrincipal MemberDetails principal) {
//        String email = principal.getAttribute("email");
        return ApiResponse.createSuccess(addressService.getAddressInfo(principal));
    }

    @GetMapping("/by-name")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<?> getAddressInfoByAddrName(@AuthenticationPrincipal MemberDetails principal,
                                             @RequestParam String addrName) {
        // 서비스 로직을 호출하여 배송지 정보 조회
        return ApiResponse.createSuccess(addressService.getAddressInfoByAddrName(principal, addrName));
    }
}
