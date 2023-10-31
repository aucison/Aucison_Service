package com.example.aucison_service.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberUpdateDto {
    private String nickname;
    private String phone;
}