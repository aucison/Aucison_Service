package com.example.aucison_service.security;

import com.example.aucison_service.enums.Role;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.service.member.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.*;

//JWT 토큰을 생성하고 검증하는 클래스를 구현
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long validityInMilliseconds;

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // 사용자 정보를 조회하는 서비스

    public String createToken(String username, Role role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("auth", role.name());

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

//    public OAuth2User getOAuth2User(String token) {
//        String email = getEmailFromToken(token);
//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put("email", email);
//        // 추가로 필요한 사용자 속성을 attributes 맵에 추가합니다.
//        // 예를 들어, 이름이나 역할 등을 추가할 수 있습니다.
//        logger.info("JwtTokenProvider - Extracted email from token: {}", email); // 이메일 추출 로그
//
//        List<GrantedAuthority> authorities = Collections.singletonList(
//                new SimpleGrantedAuthority("ROLE_USER") // 실제 역할에 맞게 설정
//        );
//
//        return new DefaultOAuth2User(authorities, attributes, "email");
//    }

    public String getEmailFromToken(String token) {
        try {
            String email = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody().getSubject();
//            logger.info("JwtTokenProvider - Extracted email from token: {}", email); // 이메일 추출 로그
            return email;
        } catch (Exception e) {
            logger.error("JwtTokenProvider - Error extracting email from token: ", e); // 에러 로그
            throw e;
        }
    }

    public Role getRoleFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody();
        String roleStr = (String) claims.get("auth");
        return Role.valueOf(roleStr);
    }

    // 토큰의 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token);    //240126 getBytes 추가
            return true;
        } catch (JwtException | IllegalArgumentException e) {
//            logger.error("Token validation error: ", e);
//            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
            //2024-05-23 수정
            return false;
        }
    }

    // Authentication 객체 생성
    //토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email); // 데이터베이스에서 사용자 정보 조회
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    // 토큰에서 권한 정보 가져오기
    private Collection<? extends GrantedAuthority> getAuthorities(String token) {
        Role role = getRoleFromToken(token);
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    // HTTP 요청으로부터 JWT 토큰 추출
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
//        logger.info("resolveToken - Extracted bearerToken: {}", bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}