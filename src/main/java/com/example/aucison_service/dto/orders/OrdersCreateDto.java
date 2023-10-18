package com.example.aucison_service.dto.orders;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrdersCreateDto {
    //주문정보 저장 시 사용하는 dto
    private Long productsId;    //상품 id
    private String email;   //구매자 이메일
//    private LocalDateTime createdTime;  //주문일자
    private Enum status;    //주문상태
}
