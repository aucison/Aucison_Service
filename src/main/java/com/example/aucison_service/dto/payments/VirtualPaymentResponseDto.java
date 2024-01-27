package com.example.aucison_service.dto.payments;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Builder
public class VirtualPaymentResponseDto {
    //가상 결제 페이지 조회 시 사용하는 dto
    private String category;    //경매 여부(경매/판매)
    private String kind;    //일반/핸드메이드

    private String productName; //상품명
    private String productImg; //상품 사진
    private float nowPrice; //(경매) 응찰가
    private float price;    //(비경매) 등록가격

    private String addrName;    //배송지명
    private String name;    //받는분 이름
    private String tel; //받는분 전화번호
    private String zipCode; //우편번호
    private String addr;    //주소
    private String addrDetail;  //상세주소
    private float credit;   //현재자산
    private float newCredit;    //잔액(남은자산)
}