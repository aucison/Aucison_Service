package com.example.aucison_service.security;

import com.example.aucison_service.service.member.CustomUserDetailsService;
import com.example.aucison_service.util.JwtUtils;
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

    @Autowired
    public JwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
    }

    //요청이 들어올 때마다 실행되며 토큰을 파싱하고 인증을 처리하는 메소드
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request); //요청으로 부터 Jwt 추출
            if (jwt != null && jwtUtils.validateToken(jwt)) {   //토큰이 실제 존재하고 jwtUtils를 통해 유효한지(만료되지 않으며 올바른 서명)
                String userEmail = jwtUtils.getEmailFromToken(jwt); //이메일 추출

                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);   //이메일 이용하여 사용자 상세정보 로드
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());  //사용자의 상세 정보와 권한 정보를 포함하는 인증 토큰을 생성, 비밀번호 이미 토큰에 들어왔으므로 null 설정
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));  //현재 요청의 컨텍스트(세션 ID, IP 등) 정보를 인증 토큰에 추가, 부수작업용

                SecurityContextHolder.getContext().setAuthentication(authentication);   //현재의 보안 컨텍스트에 새로 생성한 인증 토큰을 설정, 이후 해당 사용자가 인증된 것으로 판단함
            }
        } catch (Exception e) {
            logger.error("사용자 인증을 설정할 수 없습니다: {}", e);
        }

        filterChain.doFilter(request, response);    //이 필터가 요청을 처리한 후 필터 체인의 다음 필터에 요청과 응답을 전달, 만약 마지막 필터라면 실제 요청의 목적지(컨트롤러 등)에 도달하여 요청이 처리
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
}