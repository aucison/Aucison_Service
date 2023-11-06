package com.example.aucison_service.controller;

import com.example.aucison_service.security.JwtTokenProvider;
import com.example.aucison_service.service.member.GoogleAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final GoogleAuthService googleAuthService;

    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthController(GoogleAuthService googleAuthService, JwtTokenProvider tokenProvider) {
        this.googleAuthService = googleAuthService;
        this.tokenProvider = tokenProvider;
    }

    //사용자가 구글 로그인을 완료 -> 구글은 code 쿼리 파라미터를 포함하여 /google/callback 엔드포인트로 사용자를 리디렉션

    // Google 로그인 페이지로 리디렉션
    @GetMapping("/login/google")
    public ResponseEntity<?> redirectToGoogle() {
        String url = googleAuthService.createGoogleAuthorizationURL();   //OAuth 2.0 인증을 위한 URL을 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));   //HTTP 헤더에 Location을 설정하여 생성된 URL로 리디렉션하도록 지시
        logger.info("111");
        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER); // 클라이언트에게 GET 방식으로 다른 URI로 리디렉션하라는 명령을 내림
    }


    // Google 콜백 처리
    @GetMapping("/google/callback")
    public Mono<ResponseEntity<?>> handleGoogleCallback(@RequestParam(name = "code") String code) {
        logger.info("222");
        return googleAuthService.exchangeCodeForToken(code)
                // 여기에서 JWT를 생성하고 사용자 정보를 처리하는 추가적인 로직을 적용할 수 있음
                // 예를 들어 JWT 토큰을 생성하고 이를 클라이언트에게 반환할 수 있음
                .flatMap(token -> googleAuthService.registerOrLoginUser(token)
                        .map(member -> {
                            // 토큰 생성 및 추가 처리를 위한 로직
                            // 예를 들어, JWT 토큰 생성
                            String jwt = tokenProvider.createToken(member.getEmail(), member.getRole());

                            // 성공 로그 추가
                            logger.info("Login successful for user: {}", member.getEmail());



                            // 클라이언트에 반환될 응답
                            HttpHeaders headers = new HttpHeaders();
                            headers.add("Authorization", "Bearer " + jwt);
                            // 사용자 정보와 JWT 토큰을 함께 반환
                            return new ResponseEntity<>(member, headers, HttpStatus.OK);
                        })
                );
    }


//
//    // 구글 로그아웃을 처리하는 엔드포인트
//    @PostMapping("/google/logout")
//    public ResponseEntity<String> googleLogout() {
//        // 세션을 무효화하거나 인증 방법에 따라 JWT 토큰을 제거
//        //  httpSession.invalidate();   -> 서버측에서 세션을 무효화할 필요가 없음
//        // 성공 메시지 또는 상태를 반환합
//        return ResponseEntity.ok().body("성공적으로 로그아웃되었습니다.");
//    }
//
//    // 사용자 삭제 또는 비활성화를 처리하는 엔드포인트
//    @DeleteMapping("/user/{id}")
//    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
//        // 사용자가 존재하는지 확인하고 사용자 계정을 삭제하거나 비활성화
//        boolean deleted = googleAuthService.deleteUser(id);
//        if (deleted) {
//            return ResponseEntity.ok().body("사용자가 성공적으로 삭제되었습니다.");
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
//        }
//    }

//
//
//    // 회원 정보 가져오기
//    @GetMapping("/member-info")
//    public ResponseEntity<MembersInfoDto> getMemberInfo(HttpServletRequest request) {
//        String jwt = jwtUtils.parseJwt(request);
//        if (jwt == null || !jwtUtils.validateToken(jwt)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 유효하지 않은 접근
//        }
//
//        String userEmail = jwtUtils.getEmailFromToken(jwt);
//        MembersInfoDto memberInfo = authService.getMemberInfo(userEmail); // 수정된 부분
//
//        return ResponseEntity.ok(memberInfo);
//    }
//
//    // 회원 정보 업데이트
//    @PutMapping("/update-info")
//    public ResponseEntity<Void> updateMemberInfo(HttpServletRequest request, @RequestBody MemberUpdateDto updateDto) {
//        String jwt = jwtUtils.parseJwt(request);
//        if (jwt == null || !jwtUtils.validateToken(jwt)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 유효하지 않은 접근
//        }
//
//        String userEmail = jwtUtils.getEmailFromToken(jwt);
//        authService.updateMemberInfo(userEmail, updateDto);
//
//        return ResponseEntity.ok().build();
//    }

}
