package com.example.aucison_service.service.member;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleService {

    // Google client ID
    private final String googleClientId = "509320414110-n3vuvelvfthqjkmt2s842mf95ieohf62.apps.googleusercontent.com";

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