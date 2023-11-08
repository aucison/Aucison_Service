package com.example.aucison_service.dto.auth;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleTokenResponseDto {
    @JsonProperty("access_token")
    private String accessToken; // 액세스 토큰
    @JsonProperty("refresh_token")
    private String refreshToken; // 새 액세스 토큰을 얻는 데 사용할 수 있는 토큰
    @JsonProperty("token_type")
    private String tokenType; // 토큰 유형, 예를 들어 "Bearer"
    @JsonProperty("expires_in")
    private Long expiresIn; // 액세스 토큰의 수명
    @JsonProperty("id_token")
    private String idToken; // 사용자 식별 정보를 포함하는 토큰
}
