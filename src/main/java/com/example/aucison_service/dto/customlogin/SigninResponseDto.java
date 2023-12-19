package com.example.aucison_service.dto.customlogin;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SigninResponseDto {
    private Long userId;
    private String email;
    private String token;
}
