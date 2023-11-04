package com.example.aucison_service.dto.auth;

import com.example.aucison_service.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private Long userId;
    private String email;
    private String name;
    private String nickname;
    private Role role;
}