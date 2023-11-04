package com.example.aucison_service.dto.auth;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleRequestDto {

    private String idToken; // Google에서 받아온 ID 토큰
}
