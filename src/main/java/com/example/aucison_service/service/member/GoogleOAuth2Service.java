package com.example.aucison_service.service.member;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;

public class GoogleOAuth2Service {

    private static final String CREDENTIALS_FILE_PATH = "/path/to/your/credentials.json";

    // GoogleCredentials 객체를 사용하여 OAuth 2.0 토큰을 가져옵니다.
    public AccessToken getGoogleAccessToken() throws IOException {
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(CREDENTIALS_FILE_PATH))
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));

        credentials.refreshIfExpired(); // 이 메소드는 토큰이 만료된 경우에만 새로고침을 수행합니다.
        return credentials.getAccessToken(); // 현재의 액세스 토큰을 반환합니다.
    }

    // 토큰 정보를 출력하거나 기타 로직을 처리합니다.
    public void processAccessToken() {
        try {
            AccessToken token = getGoogleAccessToken();

            if (token != null) {
                System.out.println("Access token: " + token.getTokenValue());
                // 필요한 추가 로직을 여기에 구현할 수 있습니다.
            } else {
                System.out.println("Access token is invalid or expired.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // 적절한 예외 처리를 수행합니다.
        }
    }

    // 메인 메서드에서 서비스의 기능을 테스트할 수 있습니다.
    public static void main(String[] args) {
        GoogleOAuth2Service authService = new GoogleOAuth2Service();
        authService.processAccessToken();
    }
}