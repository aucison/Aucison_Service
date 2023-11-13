package com.example.aucison_service.dto.mypage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseAddressDto {
    private String addrName; // 배송지명(사용자 지정값)
    private String name; // 받는 분 성함
    private String zipNum; // 우편번호
    private String addr; // 주소
    private String addrDetail; // 상세주소(동, 호수)
    private String tel; // 받는 분 전화번호
}
