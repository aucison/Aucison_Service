package com.example.aucison_service.dto.auth;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleTokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
}
