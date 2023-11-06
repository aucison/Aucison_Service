package com.example.aucison_service.dto.auth;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleTokenResponseDto {
    private String accessToken; // 액세스 토큰
    private String refreshToken; // 새 액세스 토큰을 얻는 데 사용할 수 있는 토큰
    private String tokenType; // 토큰 유형, 예를 들어 "Bearer"
    private Long expiresIn; // 액세스 토큰의 수명
    private String idToken; // 사용자 식별 정보를 포함하는 토큰
}
