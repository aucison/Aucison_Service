package com.example.aucison_service.service.member;

import com.example.aucison_service.enums.Role;
import com.example.aucison_service.jpa.member.MembersEntity;
import com.example.aucison_service.jpa.member.MembersRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;


@Service
public class GoogleAuthService {

    // Google OAuth2 정보
    private final String clientId;
    private final String clientSecret;

    private final MembersRepository membersRepository;

    @Autowired
    public GoogleAuthService(@Value("${google.oauth2.client-id}") String clientId,
                             @Value("${google.oauth2.client-secret}") String clientSecret,
                             MembersRepository membersRepository ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.membersRepository = membersRepository;
    }


    // Google ID 토큰을 검증하는 메소드
    public GoogleIdToken verifyToken(String idTokenString) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                return idToken;
            } else {
                throw new InvalidTokenException("ID token is null");
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new InvalidTokenException("Invalid ID token", e);
        }
    }

    // ID 토큰에서 사용자 정보를 추출하여 MembersEntity에 저장하는 메소드
    @Transactional
    public MembersEntity authenticateUser(GoogleIdToken.Payload payload) {
        // 사용자 확인 및 생성
        MembersEntity user = ensureUserExists(payload);

        // 사용자 정보 업데이트
        updateUserWithGoogleData(user, payload);

        // 사용자 저장
        return saveUser(user);
    }

    // 사용자가 존재하지 않을 경우 새로 생성하고
    // 이미 존재하는 경우에는 해당 사용자 객체를 반환
    private MembersEntity ensureUserExists(GoogleIdToken.Payload payload) {
        return findByEmail(payload.getEmail())
                .orElseGet(() -> createNewUser(payload));
    }


    // Google에서 제공한 정보를 사용하여 사용자의 정보를 업데이트
    private void updateUserWithGoogleData(MembersEntity user, GoogleIdToken.Payload payload) {
        user.updateFromGoogle(payload);
    }


    // 이메일로 기존 사용자를 조회하는 메소드
    private Optional<MembersEntity> findByEmail(String email) {
        return membersRepository.findByEmail(email);
    }

    // 새 사용자를 생성하는 메소드
    private MembersEntity createNewUser(GoogleIdToken.Payload payload) {
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String nickname = email.contains("@") ? email.substring(0, email.indexOf("@")) : name + "_google";

        return MembersEntity.builder()
                .email(email)
                .name(name)
                .nickname(nickname) // 이메일 앞부분 또는 이름에 '_google'을 붙여 초기 닉네임 설정
                .role(Role.ROLE_CUSTOMER) // 기본 역할 ROLE_CUSTOMER로 설정
                .build();
    }

    // 사용자 정보를 데이터베이스에 저장하는 메소드
    private MembersEntity saveUser(MembersEntity user) {
        return membersRepository.save(user);
    }
}





