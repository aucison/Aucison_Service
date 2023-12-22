package com.example.aucison_service.dto.auth;

import lombok.Data;

@Data
public class MemberAdditionalInfoRequestDto {
    private String nickName;  //별명
    private String phone; //전화번호
    private String subEmail;    //수신 이메일
}
