package com.example.aucison_service.service.member;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleService {

    // Google client ID
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    // GoogleIdToken을 검증하기 위한 파서
    private final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
            .setAudience(Collections.singletonList(googleClientId))
            .build();

    public GoogleIdToken.Payload verify(String idToken) {
        try {
            GoogleIdToken token = verifier.verify(idToken);
            if (token != null) {
                return token.getPayload();
            }
        } catch (Exception e) {
            throw new RuntimeException("Google Token 검증 실패", e);
        }
        throw new RuntimeException("Google Token 검증 실패");
    }
}