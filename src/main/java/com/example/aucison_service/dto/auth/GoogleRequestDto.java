package com.example.aucison_service.dto.auth;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//@Getter
//@Setter
//@Builder
//public class GoogleRequestDto {
//
//    private String idToken; // Google에서 받아온 ID 토큰
//}

@Data
@Builder
public class GoogleRequestDto {
    private String idToken;

    // 기본 생성자
    public GoogleRequestDto() {}

    // 생성자 추가
    public GoogleRequestDto(String idToken) {
        this.idToken = idToken;
    }

    // Getter, Setter ...
}
