package com.example.aucison_service.dto.customlogin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SigninRequestDto {
    private String email;
    private String password;
    private String name;
    private String nickname;
}
