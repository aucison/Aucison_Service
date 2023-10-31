package com.example.aucison_service.dto.auth;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GoogleResponseDto {
    private String accessToken;
    private String refreshToken;
}
