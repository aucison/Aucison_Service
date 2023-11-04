package com.example.aucison_service.dto.auth;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class GoogleRequestDto {

    private String idToken; // Google에서 받아온 ID 토큰
}
