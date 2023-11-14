package com.example.aucison_service.dto.auth;

import com.example.aucison_service.jpa.member.MembersEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private MembersEntity member;
    private String jwtToken;
}