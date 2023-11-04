package com.example.aucison_service.dto.auth;


<<<<<<< HEAD
import lombok.*;

=======
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

>>>>>>> 2e63f1b1f26e4ab9eb36edde5b8afffb3a65c5cd
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
