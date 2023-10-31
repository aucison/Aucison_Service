package com.example.aucison_service.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MembersInfoDto {
    private String email; // 구글 이메일
    private String name; // 구글 이름
    private String nickName; // 별명


    private String subEmail; // 서브 이메일
    private String phone; // 전화번호
    private String imgUrl; // 프로필 사진 URL////////////수정필요
}
