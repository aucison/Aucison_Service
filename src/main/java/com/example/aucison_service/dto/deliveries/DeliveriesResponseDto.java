package com.example.aucison_service.dto.deliveries;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeliveriesResponseDto {
    //orders_id 에 따른 배송지 조회에 사용하는 dto
    private String addrName; // 배송지명
    private String name;     // 받는분 성함
    private String zipNum;  //우편번호
    private String addr;     // 주소
    private String addrDetail; // 상세주소
    private String tel;        // 받는분 전화번호
    private boolean isCompleted; // 배송완료여부
    private boolean isStarted;   // 배송시작여부
}
