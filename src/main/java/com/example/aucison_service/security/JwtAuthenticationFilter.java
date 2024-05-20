package com.example.aucison_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    @Autowired
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // HTTP 요청의 헤더에서 JWT를 추출하고 인증을 수행합니다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
//        logger.info("JwtAuthenticationFilter - Authorization Header: {}", authorizationHeader); // 헤더 로그 출력
        try {
            String token = jwtTokenProvider.resolveToken(request);
//            logger.info("JwtAuthenticationFilter - Token: {}", token); // 토큰 로그 추가
            if (token != null && jwtTokenProvider.validateToken(token)) {
                //OAuth2User oAuth2User = jwtTokenProvider.getOAuth2User(token);
                //Authentication auth = new UsernamePasswordAuthenticationToken(oAuth2User, token, oAuth2User.getAuthorities());
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            logger.error("Authentication error: ", e);
        }
        filterChain.doFilter(request, response);
    }
}