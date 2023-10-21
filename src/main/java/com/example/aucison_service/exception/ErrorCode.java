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
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다.");



    private final HttpStatus httpStatus;
    private final String message;

}
