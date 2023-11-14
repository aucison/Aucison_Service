package com.example.aucison_service.dto.mypage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestAddressDto {
    private String addrName; // 배송지명
    private String name; // 받는 사람 이름
    private String tel; // 전화번호
    private String addr; // 주소
    private String zipNum; // 우편번호
    private String addrDetail; // 상세주소
}
