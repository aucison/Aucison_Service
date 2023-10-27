package com.example.aucison_service.security;

import com.example.aucison_service.service.member.CustomUserDetailsService;
import com.example.aucison_service.util.JwtUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter { //OncePerRequestFilter를 상속받아 HTTP 요청 당 한 번씩 호출되도록 보장

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    private final GoogleIdTokenVerifier googleIdTokenVerifier;  // Google ID 토큰 검증기
    @Autowired
    public JwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailsService customUserDetailsService, GoogleIdTokenVerifier googleIdTokenVerifier) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
    }

    //요청이 들어올 때마다 실행되며 토큰을 파싱하고 인증을 처리하는 메소드
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null) {
                if (isGoogleIdToken(jwt)) {
                    //Google ID 토큰 처리
                    processGoogleIdToken(jwt, request);
                } else if (jwtUtils.validateToken(jwt)) {
                    //기존 JWT 처리
                    processStandardJwtToken(jwt, request);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }


    //HTTP 요청 헤더에서 Authorization 값을 읽어오는 메소드
    //이 값이 "Bearer "로 시작하는 경우 -> 실제 토큰은 이 접두어 다음의 문자열
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {  //유효할 경우
            return headerAuth.substring(7);
        }

        return null;
    }


    private boolean isGoogleIdToken(String jwt) {
        try {
            // 토큰 검증 시도; 유효하면 true 반환
            return googleIdTokenVerifier.verify(jwt) != null;
        } catch (Exception e) {
            // 검증 중 오류 발생 시 로깅 및 false 반환
            logger.error("Failed to verify Google ID Token: {}", e);
            return false;
        }
    }

    private void processGoogleIdToken(String jwt, HttpServletRequest request) throws Exception {
        // Google ID 토큰 검증 및 처리
        GoogleIdToken idToken = googleIdTokenVerifier.verify(jwt);
        if (idToken != null) {
            // ID 토큰이 유효한 경우
            // 필요한 정보(이메일 등) 추출
            String userEmail = idToken.getPayload().getEmail();

            // 사용자 상세 정보 로드
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);

            // 인증 객체 생성 및 설정
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private void processStandardJwtToken(String jwt, HttpServletRequest request) throws Exception {

        String userEmail = jwtUtils.getEmailFromToken(jwt); //이메일 추출

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);   //이메일 이용하여 사용자 상세정보 로드
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());  //사용자의 상세 정보와 권한 정보를 포함하는 인증 토큰을 생성, 비밀번호 이미 토큰에 들어왔으므로 null 설정
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));  //현재 요청의 컨텍스트(세션 ID, IP 등) 정보를 인증 토큰에 추가, 부수작업용

        SecurityContextHolder.getContext().setAuthentication(authentication);   //현재의 보안 컨텍스트에 새로 생성한 인증 토큰을 설정, 이후 해당 사용자가 인증된 것으로 판단함

    }

}