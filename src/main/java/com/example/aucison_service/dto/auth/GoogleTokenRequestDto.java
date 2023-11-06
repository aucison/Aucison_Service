package com.example.aucison_service.dto.auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleTokenRequestDto {
    private String clientId; // 구글 애플리케이션 클라이언트 ID
    private String clientSecret; // 구글 애플리케이션 클라이언트 비밀번호
    private String code; // 구글 인증 서버로부터 받은 코드
    private String redirectUri; // 리디렉션 URI
    private String grantType; // 부여 유형, "authorization_code"로 설정
}
