
package com.example.aucison_service.controller;


import com.example.aucison_service.dto.ApiResponse;
import com.example.aucison_service.dto.wish.WishRequestDto;
import com.example.aucison_service.dto.wish.WishResponseDto;
import com.example.aucison_service.dto.wish.WishSimpleResponseDto;
import com.example.aucison_service.service.member.MemberDetails;
import com.example.aucison_service.service.member.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/wishes")
public class WishController {

    private final WishService wishService;

    // 찜 추가
    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<WishSimpleResponseDto> addWish(@RequestBody WishRequestDto wishRequestDto,
                                                      @AuthenticationPrincipal MemberDetails principal){
        return ApiResponse.createSuccess(wishService.addWish(wishRequestDto, principal));
    }

    // 찜 삭제
//    @DeleteMapping("/delete")
//    @PreAuthorize("isAuthenticated()")
//    public ApiResponse<WishSimpleResponseDto> deleteWish(@RequestBody WishRequestDto wishRequestDto,
//                                        @AuthenticationPrincipal MemberDetails principal) {
//
//        return ApiResponse.createSuccess(wishService.deleteWish(wishRequestDto, principal));
//    }

    @DeleteMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<WishSimpleResponseDto> deleteWish(@RequestParam(name = "productsId") Long productsId,
                                        @AuthenticationPrincipal MemberDetails principal) {

        return ApiResponse.createSuccess(wishService.deleteWish(productsId, principal));
    }


    // 사용자의 찜 목록 조회
    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<WishResponseDto>> getMemberWishList(@AuthenticationPrincipal MemberDetails principal) {
        return ApiResponse.createSuccess(wishService.getMemberWishList(principal));
    }

}
