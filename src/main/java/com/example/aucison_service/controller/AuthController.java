package com.example.aucison_service.controller;

import com.example.aucison_service.util.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/")
public class AuthController {

    private final JwtUtils jwtUtils;

    private final AuthenticationManager authenticationManager;

    public AuthController(JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // 1. 사용자 인증 요청 검증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // 2. 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. JWT 토큰 생성
        MemberDto member = (MemberDto) authentication.getPrincipal(); // Assuming 'MemberDto' is your user model class
        String accessToken = jwtUtils.createAccessToken(member);
        String refreshToken = jwtUtils.createRefreshToken(member);

        //모놀리틱 변경으로 필요없어짐 -> 자동화됨
        // 4. redis에 refresh token 저장
        //jwtUtils.updateRefreshToken(member, refreshToken);

        // 5. 토큰 반환
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/reissue")
    public ResponseEntity reissue(@RequestHeader("refreshToken") String refreshToken) {
        return reissueToken(refreshToken);
    }

    /*
    //msa 적용으로 인해 해당코드 수정
    public ResponseEntity reissueToken(String refreshToken) { // 토큰 재발행
        // refreshToken에서 이메일을 추출
        // jwtUtils는 JWT를 파싱하여 payload에 저장된 이메일을 반환하는 메서드를 제공
        String email = jwtUtils.getEmailFromToken(refreshToken);

        // 토큰이 더 이상 사용되지 않도록 삭제
        jwtUtils.deleteRefreshToken(email);

        // 이메일 정보를 사용하여 login 서비스 호출
        String loginServiceUrl = "member-service-URL"; // 로그인 서비스의 URL을 설정해야함
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        String requestBody = "{\"email\": \"" + email + "\"}";  // 이메일을 JSON 형식으로 전달
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // POST 요청을 통해 login 서비스 호출, 로그인 서비스를 호출하여 새로운 토큰을 받음
        ResponseEntity<String> loginResponse = restTemplate.exchange(
                loginServiceUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // login 서비스의 응답을 그대로 반환, 응답에는 새로운 access 토큰이 포함
        return loginResponse;
    }
     */
    public ResponseEntity<?> reissueToken(String refreshToken) {

        if (!jwtUtils.validateToken(refreshToken)) {
            return ResponseEntity.badRequest().body("Invalid refresh token");
        }

        // refreshToken에서 이메일 추출
        String email = jwtUtils.getEmailFromToken(refreshToken);

        //모놀리틱으로 주석처리
        // refreshToken 삭제
        //jwtUtils.deleteRefreshToken(email);

        // 내부 메소드를 호출하여 로직 수행 (외부 서비스 호출 대신)
        // 예를 들어, 사용자 인증 및 토큰 재발급 로직
        // 이 부분은 실제 사용자 인증 로직에 따라 다를 수 있음
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email); // 사용자 정보 로드


        String newAccessToken = jwtUtils.createAccessToken(userDetails); // 새 액세스 토큰 생성
        String newRefreshToken = jwtUtils.createRefreshToken(userDetails); // 새 리프레시 토큰 생성

        // 토큰 반환
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);

        return ResponseEntity.ok(tokens);
    }
}
