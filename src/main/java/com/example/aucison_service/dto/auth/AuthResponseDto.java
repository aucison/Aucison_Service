package com.example.aucison_service.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDto {
//    private MembersEntity member;
    private String jwtToken;
    private boolean isNewUser; // 새 사용자 여부
}