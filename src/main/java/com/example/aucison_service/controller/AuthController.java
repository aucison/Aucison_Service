package com.example.aucison_service.controller;

import com.example.aucison_service.dto.auth.GoogleRequestDto;
import com.example.aucison_service.dto.auth.GoogleResponseDto;
import com.example.aucison_service.dto.auth.MemberDto;
import com.example.aucison_service.dto.auth.MembersInfoDto;
import com.example.aucison_service.service.member.AuthService;
import com.example.aucison_service.service.member.GoogleService;
import com.example.aucison_service.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;


    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
        this.jwtUtils = new JwtUtils();
    }



    // 구글 로그인
    // 구글 로그인 처리
    @PostMapping("/google/login")
    public ResponseEntity<?> authenticateGoogleUser(@RequestBody GoogleRequestDto requestDto) {
        GoogleResponseDto responseDto = authService.authenticateGoogleUser(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    // 구글 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = jwtUtils.parseJwt(request);
        if (token != null) {
            authService.addTokenToBlacklist(token);
        }
        return ResponseEntity.ok().build();
    }



    // 회원 정보 가져오기
    @GetMapping("/member-info")
    public ResponseEntity<MembersInfoDto> getMemberInfo(HttpServletRequest request) {
        String jwt = jwtUtils.parseJwt(request);
        if (jwt == null || !jwtUtils.validateToken(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 유효하지 않은 접근
        }

        String userEmail = jwtUtils.getEmailFromToken(jwt);
        MembersInfoDto memberInfo = authService.getMemberInfo(userEmail); // 수정된 부분

        return ResponseEntity.ok(memberInfo);
    }

    // 회원 정보 업데이트

}
