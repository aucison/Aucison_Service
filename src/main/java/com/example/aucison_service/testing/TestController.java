package com.example.aucison_service.testing;

import com.example.aucison_service.dto.auth.AuthResponseDto;
import com.example.aucison_service.enums.Role;
import com.example.aucison_service.jpa.member.MembersEntity;
import com.example.aucison_service.jpa.member.MembersRepository;
import com.example.aucison_service.security.JwtTokenProvider;
import com.example.aucison_service.service.member.GoogleAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/test")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthService.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final MembersRepository membersRepository;

    @Autowired
    public TestController(JwtTokenProvider jwtTokenProvider, MembersRepository membersRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.membersRepository = membersRepository;
    }

    @GetMapping("/generate-token")
    public ResponseEntity<AuthResponseDto> generateTestToken() {
        // 테스트용 사용자 정보 설정
        String testEmail = "test@example.com";
        String testName = "Test User";
        String testNickname = testName + "_nickname";
        Role testRole = Role.ROLE_CUSTOMER;

        // 데이터베이스에서 테스트 사용자 조회
        Optional<MembersEntity> existingUser = membersRepository.findByEmail(testEmail);
        MembersEntity user;
        boolean isNewUser = false;

        if (!existingUser.isPresent()) {
            // 테스트 사용자가 없을 경우, 새로 생성
            isNewUser = true;
            user = MembersEntity.builder()
                    .email(testEmail)
                    .name(testName)
                    .nickname(testNickname)
                    .role(testRole)
                    .build();
            membersRepository.save(user);
        } else {
            // 이미 존재하는 사용자인 경우
            user = existingUser.get();
        }

        // JWT 토큰 생성
        String jwtToken = jwtTokenProvider.createToken(testEmail, testRole);
        logger.info("Generated JWT Token for test: {}", jwtToken);

        // AuthResponseDto 객체 생성 및 반환
        AuthResponseDto authResponse = AuthResponseDto.builder()
                .jwtToken(jwtToken)
                .isNewUser(isNewUser)
                .build();

        return ResponseEntity.ok(authResponse);
    }

}
