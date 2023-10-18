package com.example.aucison_service.dto.deliveries;

import lombok.Data;

@Data
public class DeliveriesCreateDto {
    // 배송 정보 저장 시 사용하는 dto
    private String addrName;    //배송지명
    private String zipNum;  //우편번호
    private String addr;    //주소
    private String addrDetail;  //상세주소(동, 호수)
    private String name;    //받는분 성함
    private String tel; //받는분 전화번호
    private boolean isCompleted;    //배송완료 여부
    private boolean isStarted;  //배송시작 여부
}
