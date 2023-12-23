package com.example.aucison_service.controller;

import com.example.aucison_service.dto.ApiResponse;
import com.example.aucison_service.dto.auth.AuthResponseDto;
import com.example.aucison_service.dto.auth.MemberAdditionalInfoRequestDto;
import com.example.aucison_service.security.JwtTokenProvider;
import com.example.aucison_service.service.member.GoogleAuthService;
import com.example.aucison_service.service.member.MemberDetails;
import com.example.aucison_service.service.member.MemberInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final GoogleAuthService googleAuthService;
    private final MemberInfoService memberInfoService;

    @Autowired
    public AuthController(GoogleAuthService googleAuthService, MemberInfoService memberInfoService) {
        this.googleAuthService = googleAuthService;
        this.memberInfoService = memberInfoService;
    }

    /*
    사용자가 Google을 통해 로그인하면, Google로부터 인증 코드를 받아 해당 코드를 사용해 액세스 토큰을 얻고,
    이를 통해 사용자 정보를 검증한 후 JWT 토큰을 생성하여 반환
    */
    // 구글 로그인 요청을 처리하는 엔드포인트
//    @GetMapping("/login/google")
//    public ResponseEntity<?> googleLogin() {
//        String url = googleAuthService.createGoogleAuthorizationURL();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Location", url);
//        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
//    }



//    // 구글 로그인 콜백을 처리하는 엔드포인트
// 구글 로그인 콜백을 처리하는 엔드포인트 수정
    @GetMapping("/google/callback")
    public ResponseEntity<?> googleCallback(@RequestParam("accessToken") String accessToken) {
        try {
            // Google로부터 액세스 토큰을 얻습니다.
            //GoogleTokenResponseDto tokenResponse = googleAuthService.getGoogleAccessToken(code);
            // 액세스 토큰을 사용하여 사용자 정보를 가져오고 JWT 토큰을 생성합니다.
            AuthResponseDto authResponse = googleAuthService.authenticateUserAndGetJwtToken(accessToken);
            // 생성된 JWT 토큰을 프론트엔드에 반환합니다.
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            logger.error("Error during Google callback processing: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing Google login");
        }
    }

    @PostMapping("/member-info")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<?> saveMemberInfo(@AuthenticationPrincipal MemberDetails principal,
                                            @RequestBody MemberAdditionalInfoRequestDto requestDto) {
        memberInfoService.saveMemberAdditionalInfo(principal, requestDto);
        return ApiResponse.createSuccessWithNoData("사용자 추가정보 생성 성공");
    }



//    @GetMapping("/google/callback")
//    public ResponseEntity<AuthResponseDto> googleCallback(@RequestParam("code") String code) {
//        GoogleTokenResponseDto tokenResponse = googleAuthService.getGoogleAccessToken(code);
//        AuthResponseDto authResponse = googleAuthService.authenticateUserAndGetJwtToken(tokenResponse);
//        return ResponseEntity.ok(authResponse);
//    }



    //사용자가 구글 로그인을 완료 -> 구글은 code 쿼리 파라미터를 포함하여 /google/callback 엔드포인트로 사용자를 리디렉션

//    // Google 로그인 페이지로 리디렉션
//    @GetMapping("/login/google")
//    public void redirectToGoogle(@RequestParam(name = "client_domain", required = false) String clientDomain, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        if (clientDomain != null) {
//            request.getSession().setAttribute("client_domain", clientDomain);
//            logger.info("Received client domain: {}", clientDomain); // 로그 추가
//        } else {
//            logger.info("No client domain received");
//        }
//        logger.info("000");
//        String url = googleAuthService.createGoogleAuthorizationURL();   //OAuth 2.0 인증을 위한 URL을 생성
//        response.sendRedirect(url);
//        logger.info("111");
//
//    }
//
//    //임시 동기 변경
//    // Google 콜백 처리
////    @GetMapping("/google/callback")
////    public Mono<ResponseEntity<?>> handleGoogleCallback(@RequestParam(name = "code") String code) {
////        logger.info("222");
////        return googleAuthService.exchangeCodeForToken(code)
////                // 여기에서 JWT를 생성하고 사용자 정보를 처리하는 추가적인 로직을 적용할 수 있음
////                // 예를 들어 JWT 토큰을 생성하고 이를 클라이언트에게 반환할 수 있음
////                .flatMap(token -> googleAuthService.registerOrLoginUser(token)
////                        .map(member -> {
////                            // 토큰 생성 및 추가 처리를 위한 로직
////                            // 예를 들어, JWT 토큰 생성
////                            String jwt = tokenProvider.createToken(member.getEmail(), member.getRole());
////
////                            // 성공 로그 추가
////                            logger.info("Login successful for user: {}", member.getEmail());
////
////
////
////                            // 클라이언트에 반환될 응답
////                            HttpHeaders headers = new HttpHeaders();
////                            headers.add("Authorization", "Bearer " + jwt);
////                            // 사용자 정보와 JWT 토큰을 함께 반환
////                            return new ResponseEntity<>(member, headers, HttpStatus.OK);
////                        })
////                );
////    }
//
//    @GetMapping("/google/callback")
//    public void handleGoogleCallback(@RequestParam(name = "code") String code, HttpServletRequest request, HttpServletResponse response) {
//        try {
//            OAuth2AuthenticationToken token = googleAuthService.exchangeCodeForToken(code);
//            if (token == null) {
//                logger.error("OAuth2AuthenticationToken is null after exchange");
//                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error in token exchange");
//                //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in token exchange");
//                return;
//            }
//
//            MembersEntity member = googleAuthService.registerOrLoginUser(token);
//
//            String jwt = tokenProvider.createToken(member.getEmail(), member.getRole());
//            logger.info("Login successful for user: {}", member.getEmail());
//
//            // JWT 토큰 값을 로그로 출력
//            logger.info("Generated JWT Token: {}", jwt);
//
//            // JWT 토큰을 쿠키에 설정
//            Cookie jwtCookie = new Cookie("auth_token", jwt);
//            jwtCookie.setHttpOnly(false);
//            jwtCookie.setSecure(true); // HTTPS 환경에서만 사용
//            jwtCookie.setPath("/");
//            jwtCookie.setMaxAge(7 * 24 * 60 * 60); // 쿠키 유효 기간 설정 (예: 7일)
//            response.addCookie(jwtCookie);
//
//            // SameSite=None; Secure를 포함하는 새로운 Set-Cookie 헤더를 직접 추가
//            String cookieHeader = String.format("auth_token=%s; Path=/; Max-Age=%d; HttpOnly; Secure; SameSite=None",
//                    jwt, 7 * 24 * 60 * 60);
//            response.addHeader("Set-Cookie", cookieHeader);
//
//            //AuthResponseDto responseDto = new AuthResponseDto(member, null);
//
//            //return ResponseEntity.ok(responseDto);
//
//            // 여기에서 리디렉션을 처리
//            String clientDomain = (String) request.getSession().getAttribute("client_domain");
//            String redirectUrl = determineRedirectUrl(clientDomain);
//            logger.info("Redirecting to URL: {}", redirectUrl);
//            response.sendRedirect(redirectUrl);
//
//            //return ResponseEntity.ok(jwt);
//
////            // 로그인 성공 메시지 반환
////            return ResponseEntity.ok("Login successful");
//        } catch (Exception e) {
//            logger.error("Error during Google callback handling", e);
//            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//        }
//    }
//
//    private String determineRedirectUrl(String clientDomain) {
//        if ("https://localhost:3000".equals(clientDomain)) {
//            return "https://localhost:3000/home";
//        } else if ("https://aucison.shop:443".equals(clientDomain)) {
//            return "https://aucison.shop:443/home";
//        } else {
//            return "https://localhost:3000/home";
//        }
//    }
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
