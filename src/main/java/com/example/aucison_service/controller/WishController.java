
package com.example.aucison_service.controller;


import com.example.aucison_service.dto.wish.WishRequestDto;
import com.example.aucison_service.dto.wish.WishResponseDto;
import com.example.aucison_service.service.member.MemberDetails;
import com.example.aucison_service.service.member.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    // 찜 추가
    @PostMapping("/add")
    public ResponseEntity<?> addWish(@RequestBody WishRequestDto wishRequestDto,
                                     @AuthenticationPrincipal MemberDetails principal) {
        wishService.addWish(wishRequestDto, principal);
        return new ResponseEntity<>("Wish added successfully", HttpStatus.CREATED);
    }

    // 찜 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteWish(@RequestBody WishRequestDto wishRequestDto,
                                        @AuthenticationPrincipal MemberDetails principal) {
        wishService.deleteWish(wishRequestDto, principal);
        return new ResponseEntity<>("Wish deleted successfully", HttpStatus.OK);
    }

    // 사용자의 찜 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<WishResponseDto>> getMemberWishList(@AuthenticationPrincipal MemberDetails principal) {
        List<WishResponseDto> wishes = wishService.getMemberWishList(principal);
        return new ResponseEntity<>(wishes, HttpStatus.OK);
    }
}
