package com.example.aucison_service.dto.mypage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseMemberProfileDto {
    private String profileUrl; // 회원 프로필 URL
    private String nickname;   // 닉네임
    private String email;      // 이메일
    private String phone;      // 전화번호
}
