package com.example.aucison_service.dto.orders;


import com.example.aucison_service.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class OrdersResponseDto {
    //주문내역 조회 시 사용하는 dto
    private String productName; //상품명
    private MultipartFile productImg; //상품 사진
    private String productDescription;  //상품 간단 설명
    private String category; // 경매여부(경매/비경매)
    private Long ordersId;  //주문번호
    private String createdTime;  //주문일자(formatter로 인한 string 형 변환)
    private OrderStatus status;  //주문상태

    private float price; //구매 가격

}
