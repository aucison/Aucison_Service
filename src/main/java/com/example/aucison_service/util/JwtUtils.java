package com.example.aucison_service.util;


import com.example.aucison_service.dto.auth.GoogleLoginDto;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.util.Date;


@Component
public class JwtUtils {
    @Value("${token.access-token-time}")
    private long accessTokenTime;
    @Value("${token.refresh-token-time}")
    private long refreshTokenTime;
    @Value("${token.secret}")
    private String secretKey;


    //토큰생성 코드
    public String createAccessToken(GoogleLoginDto loginDto) {
        Claims claims = Jwts.claims();
        claims.put("email", loginDto.getEmail());

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    //토큰생성 코드
    public String createRefreshToken(GoogleLoginDto loginDto) { // 새로 만들기 전에 기존 refreshToken 지우고 만들기
        Claims claims = Jwts.claims();
        claims.put("email", loginDto.getEmail());

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /* MSA 미적용으로 주석처리 -> 간소화
    //redis와 상호작용
    public void updateRefreshToken(MemberDto memberDto, String refreshToken) { // 만든 refreshToken redis에 저장하는 함수
        // 사용자의 이메일과 refreshToken을 Redis에 저장, refreshToken의 유효 시간도 함께 설정
        stringRedisTemplate.opsForValue().set(memberDto.getEmail(), refreshToken, refreshTokenTime, TimeUnit.MILLISECONDS);
    }

    //redis와 상호작용
    public String getRefreshToken(String email) { // 사용자 구글 이메일(로그인 이메일)로 refreshToken 조회하는 함수
        return stringRedisTemplate.opsForValue().get(email);
    }

    //redis와 상호작용
    public void deleteRefreshToken(String email) { // 사용자 구글 이메일(로그인 이메일)로 refreshToken 삭제하는 함수
        if (getRefreshToken(email) != null) {
            stringRedisTemplate.delete(email);
        }
    }

     */


    // 토큰 검증 코드
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            // JWT의 서명 검증에 실패했을 때의 처리
            throw new RuntimeException("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            // JWT 형식 오류
            throw new RuntimeException("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            // JWT 유효기간 만료
            throw new RuntimeException("Expired JWT token.");
        } catch (IllegalArgumentException e) {
            // claimsJws가 비어있을 때, 예를 들어 토큰이 비어있는 경우
            throw new RuntimeException("Empty JWT token.");
        }
    }

    /*
    // 모노리틱에서는  JWT 토큰이 자체 만료 메커니즘을 가지고 있기 때문에
    // 사용자 세션이 끝나면 자동으로 만료됨
    // 따라서 로그아웃된 토큰을 추적할 필요가 없음
    //로그아웃 혹은 토큰 무효화 처리
    public void setBlackList(String accessToken) { // 로그아웃 시 redis에 만료된 accessToken이라고 저장하는 함수
        Long expiration = getExpiration(accessToken);
        stringRedisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
    }

    //토큰 검증 코드
    // Redis에 저장된 정보를 바탕으로 토큰이 로그아웃(무효화)되었는지 확인
    public boolean isLogout(String accessToken) {
        return !ObjectUtils.isEmpty(stringRedisTemplate.opsForValue().get(accessToken));
    }

     */

    //토큰 만료 확인
    public Long getExpiration(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.getTime() - new Date().getTime();
    }


    //토큰에서 사용자 정보 추출
    public String getEmailFromToken(String token) {
        return (String) getClaims(token).get("email");
    }


    public Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    //파싱 -> "Bearer " 문자열 뒤의 값을 추출하는 로직을 사용
    public String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }

/*
    // 자동화 가능하여 주석처리 -> 모놀리틱 전환
    public String resolveAccessToken(HttpServletRequest request) {
        String jwtHeader = request.getHeader("Authorization");
        if (jwtHeader != null && jwtHeader.startsWith("Bearer ")) {
            return jwtHeader.replace("Bearer ", "");
        } else {
            return null;
        }
    }

 */
}
