package com.example.aucison_service.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    PRODUCT_NOT_EXIST(HttpStatus.NOT_FOUND,"상품 목록이 존재하지 않습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."),
    SEARCH_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 검색어입니다."),
    IMG_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 이미지입니다."),
    IMAGE_PROCESSING_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다"),

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),

    // Members 관련 에러 코드들
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    // Histories 관련 에러 코드들
    HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "주문/판매 내역을 찾을 수 없습니다."),
    HISTORY_IMG_NOT_FOUND(HttpStatus.NOT_FOUND, "주문/판매 내역 사진을 찾을 수 없습니다."),

    // Deliveries 관련 에러 코드들
    DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "배송지 정보를 찾을 수 없습니다."),
    DELIVERY_SAVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "배송지 정보 저장에 실패했습니다."),

    //Orders 관련 에러 코드들
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    ORDER_SAVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "주문 정보 저장에 실패했습니다."),

    // Payments 관련 에러 코드들
    END_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품의 경매 종료 날짜를 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 카테고리(경매/판매)가 존재하지 않습니다."),
    AUCTION_ENDED(HttpStatus.FORBIDDEN, "경매가 이미 종료되었습니다."),
    INSUFFICIENT_CREDIT(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),
    SHIPPING_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "배송지를 새로 등록해주세요."),
    LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "로그를 찾을 수 없습니다.");



    private final HttpStatus httpStatus;
    private final String message;

}
