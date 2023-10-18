package com.example.aucison_service.dto;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ApiResponse<T> {
    // API 응답을 표준화하기 위한 일종의 래퍼(wrapper) 클래스


    //응답 상태를 나누는 3가지 상수
    private static final String SUCCESS_RESULT = "success";
    private static final String FAIL_RESULT = "fail";
    private static final String ERROR_RESULT = "error";


    private String result;      //API 응답 결과를 나타내는 문자열 필드( 대표적으로 success, fail, error)
    private HttpStatus httpStatus;  //http상태코드
    private String message;     //추가적인 메시지나 설명
    private T data;     //실제로 반환하려는 데이터를 담는 제네릭 필드

    //성공적인 API응답을 생성하는 정적 메소드
    public static <T> ApiResponse<T> createSuccess(T data) {
        return new ApiResponse<>(SUCCESS_RESULT, HttpStatus.OK, null, data);
    }

    //데이터 없이 성공적인 API 응담을 생성하는 정적 메소드
    public static ApiResponse<?> createSuccessWithNoData(String message) {
        return new ApiResponse<>(SUCCESS_RESULT, HttpStatus.OK, message, null);
    }

    //에러 상황에서 사용하는 정적 메소드
    public static ApiResponse<?> createError(HttpStatus httpStatus, String message) {
        return new ApiResponse<>(ERROR_RESULT, httpStatus, message, null);
    }

    //실패 상황에서 사용하는 정적 메소드
    public static ApiResponse<?> createFail(HttpStatus httpStatus, String message) {
        return new ApiResponse<>(FAIL_RESULT, httpStatus, message, null);
    }
}
