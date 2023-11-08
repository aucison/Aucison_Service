package com.example.aucison_service.dto.auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleTokenRequestDto {
    private String clientId;     // Credentials page에서 가져온 API Console 클라이언트 ID
    private String clientSecret; // Credentials page에서 가져온 API Console 클라이언트 보안 비밀번호
    private String code;         // 초기 요청에서 반환된 승인 코드
    private String redirectUri;  // 지정된 client_id의 API Console Credentials page 에서 프로젝트에 대해 나열된 리디렉션 URI 중 하나
    private String grantType;    // OAuth 2.0 사양에 정의된 대로 이 필드의 값은 "authorization_code"로 설정

    // 이 메서드는 token 요청을 위한 매개변수를 HTTP 요청 형식으로 변환합니다.
    public String toFormUrlEncoded() {
        return "code=" + this.code +
                "&client_id=" + this.clientId +
                "&client_secret=" + this.clientSecret +
                "&redirect_uri=" + this.redirectUri +
                "&grant_type=" + this.grantType;
    }
}
