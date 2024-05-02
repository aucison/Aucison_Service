package com.example.aucison_service.dto.auth;

import lombok.Data;

@Data
public class MemberAdditionalInfoRequestDto {
    private String nickName;  //별명
    private String phone; //전화번호
    private String subEmail;    //수신 이메일

    //최초 배송지는 반드시 대표배송지로 설정되기 때문에 대표 배송지 여부(isPrimary)를 받지 않는다.
    private String addrName; // 배송지명
    private String name; // 받는 사람 이름
    private String tel; // 전화번호
    private String addr; // 주소
    private String zipNum; // 우편번호
    private String addrDetail; // 상세주소
}
