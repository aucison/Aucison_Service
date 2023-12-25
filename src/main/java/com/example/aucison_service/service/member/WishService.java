package com.example.aucison_service.service.member;


import com.example.aucison_service.dto.wish.WishRequestDto;
import com.example.aucison_service.dto.wish.WishResponseDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

public interface WishService {

    //찜 추가
    //찜 삭제
    //찜 리스트 조회

    void addWish(WishRequestDto wishRequestDto, @AuthenticationPrincipal MemberDetails principal);
    void deleteWish(WishRequestDto wishRequestDto, @AuthenticationPrincipal MemberDetails principal);
    List<WishResponseDto> getMemberWishList(@AuthenticationPrincipal MemberDetails principal);
}
