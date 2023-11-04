package com.example.aucison_service.controller;

import com.example.aucison_service.dto.auth.*;
import com.example.aucison_service.jpa.member.MembersEntity;
import com.example.aucison_service.security.JwtTokenProvider;
import com.example.aucison_service.service.member.GoogleAuthService;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final GoogleAuthService googleAuthService;

    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthController(GoogleAuthService googleAuthService, JwtTokenProvider tokenProvider) {
        this.googleAuthService = googleAuthService;
        this.tokenProvider = tokenProvider;
    }



    // 구글 로그아웃을 처리하는 엔드포인트
    @PostMapping("/google/logout")
    public ResponseEntity<String> googleLogout() {
        // 세션을 무효화하거나 인증 방법에 따라 JWT 토큰을 제거
        //  httpSession.invalidate();   -> 서버측에서 세션을 무효화할 필요가 없음
        // 성공 메시지 또는 상태를 반환합
        return ResponseEntity.ok().body("성공적으로 로그아웃되었습니다.");
    }

    // 사용자 삭제 또는 비활성화를 처리하는 엔드포인트
    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        // 사용자가 존재하는지 확인하고 사용자 계정을 삭제하거나 비활성화
        boolean deleted = googleAuthService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok().body("사용자가 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
    }

//
//
//    // 회원 정보 가져오기
//    @GetMapping("/member-info")
//    public ResponseEntity<MembersInfoDto> getMemberInfo(HttpServletRequest request) {
//        String jwt = jwtUtils.parseJwt(request);
//        if (jwt == null || !jwtUtils.validateToken(jwt)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 유효하지 않은 접근
//        }
//
//        String userEmail = jwtUtils.getEmailFromToken(jwt);
//        MembersInfoDto memberInfo = authService.getMemberInfo(userEmail); // 수정된 부분
//
//        return ResponseEntity.ok(memberInfo);
//    }
//
//    // 회원 정보 업데이트
//    @PutMapping("/update-info")
//    public ResponseEntity<Void> updateMemberInfo(HttpServletRequest request, @RequestBody MemberUpdateDto updateDto) {
//        String jwt = jwtUtils.parseJwt(request);
//        if (jwt == null || !jwtUtils.validateToken(jwt)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 유효하지 않은 접근
//        }
//
//        String userEmail = jwtUtils.getEmailFromToken(jwt);
//        authService.updateMemberInfo(userEmail, updateDto);
//
//        return ResponseEntity.ok().build();
//    }

}
