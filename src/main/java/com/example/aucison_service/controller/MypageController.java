package com.example.aucison_service.controller;


import com.example.aucison_service.dto.mypage.RequestOrderDetailsDto;
import com.example.aucison_service.dto.mypage.ResponseOrderDetailsDto;
import com.example.aucison_service.service.member.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mp")
public class MypageController {


    private final MypageService mypageService;

    @GetMapping("/buy")
    public ResponseEntity getOrderInfo(@AuthenticationPrincipal OAuth2User principal) {
        // principal에서 이메일 가져오기
        String email = principal.getAttribute("email");
        return ResponseEntity.status(HttpStatus.OK).body(mypageService.getOrderHistoryList(email));
    }

    @GetMapping("/buy/{historiesId}")
    public ResponseEntity getOrderDetail(@PathVariable("historiesId") Long historiesId,
                                         @RequestHeader("accessToken") String accessToken) throws Exception {
        ResponseOrderDetailsDto responseOrderDetailsDto = mypageService.getOrderDetails(RequestOrderDetailsDto.builder().
                email(jwtUtils.getEmailFromToken(accessToken)).historiesId(historiesId).build());
        return ResponseEntity.status(HttpStatus.OK).body(responseOrderDetailsDto);
    }

    @GetMapping("/sell")
    public ResponseEntity getSellInfo(@RequestHeader("accessToken") String accessToken) {
        String email = jwtUtils.getEmailFromToken(accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(mypageService.getSellHistoryList(email));
    }
}
