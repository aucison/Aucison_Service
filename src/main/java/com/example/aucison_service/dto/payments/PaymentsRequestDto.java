package com.example.aucison_service.dto.payments;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class PaymentsRequestDto {
    //결제 저장(결제 완료) 에 사용하는 dto
    private Long productsId;    //상품 id
    private String email;   //이메일
    private MultipartFile productImg; //상품 사진
    private String category; // 경매여부(경매/비경매)
    private String addrName;    //배송지명
    private String zipNum;  //우편번호
    private String addr;    //주소
    private String addrDetail;  //상세주소
    private String name;    //받는사람 이름
    private String tel; //받는사람 전화번호
    private float nowPrice; //(경매) 응찰가: 가상 결제 페이지에서 연산이 끝난 가격
    private float price;    //(비경매) 등록가격
}