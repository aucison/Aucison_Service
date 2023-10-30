package com.example.aucison_service.controller;

import com.example.aucison_service.dto.auth.GoogleRequestDto;
import com.example.aucison_service.dto.auth.GoogleResponseDto;
import com.example.aucison_service.dto.auth.MemberDto;
import com.example.aucison_service.dto.auth.MembersInfoDto;
import com.example.aucison_service.service.member.AuthService;
import com.example.aucison_service.service.member.GoogleService;
import com.example.aucison_service.util.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final GoogleService googleService;

    // 구글 클라이언트 ID
    private static final String GOOGLE_CLIENT_ID = "509320414110-n3vuvelvfthqjkmt2s842mf95ieohf62.apps.googleusercontent.com";

    @Autowired
    public AuthController(AuthService authService, JwtUtils jwtUtils, GoogleService googleService) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
        this.googleService = googleService;
    }

    //매우주의!!!! -> 이전 코드 영역 전부 주석처리함, 10.27(금) 이후 전체 재 작성 바람, 해당부분 지우지 말고 아래에 새로 작성할 것
    /* 매우주의!!!! -> 이전 코드 영역 전부 주석처리함, 10.27(금) 이후 전체 재 작성 바람, 해당부분 지우지 말고 아래에 새로 작성할 것

   @PostMapping("/signin")
    public ResponseEntity signIn(@RequestBody RequestSignInVo request) {
//        ModelMapper mapper = new ModelMapper();
//        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//
//        MemberDto memberDto = mapper.map(request, MemberDto.class);
//        authService.createMember(memberDto);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(null);

       // RequestVO -> DTO 변환
       MemberDto memberDto = new MemberDto(request);

       authService.createMember(memberDto);

       return ResponseEntity.status(HttpStatus.CREATED).build();
   }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // 1. 사용자 인증 요청 검증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // 2. 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. JWT 토큰 생성
        MemberDto member = (MemberDto) authentication.getPrincipal(); // Assuming 'MemberDto' is your user model class
        String accessToken = jwtUtils.createAccessToken(member);
        String refreshToken = jwtUtils.createRefreshToken(member);

        //모놀리틱 변경으로 필요없어짐 -> 자동화됨
        // 4. redis에 refresh token 저장
        //jwtUtils.updateRefreshToken(member, refreshToken);

        // 5. 토큰 반환
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return ResponseEntity.ok(tokens);
    }
  
    @PostMapping("/logout")
    public ResponseEntity logOut(@RequestHeader("accessToken") String accessToken) {
        authService.logout(accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/reissue")
    public ResponseEntity reissue(@RequestHeader("refreshToken") String refreshToken) {
        return reissueToken(refreshToken);
    }


    @GetMapping("/mp")
    public ResponseEntity getMemberInfo(@RequestHeader("accessToken") String accessToken) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.getMember(accessToken));
    }

    @PatchMapping("/mp")
    public ResponseEntity patchMemberInfo(@RequestHeader("accessToken") String accessToken,
                                          @RequestBody MembersInfoDto membersInfoDto) {
        authService.patchMember(accessToken, membersInfoDto);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
  
    public ResponseEntity<?> reissueToken(String refreshToken) {

        if (!jwtUtils.validateToken(refreshToken)) {
            return ResponseEntity.badRequest().body("Invalid refresh token");
        }

        // refreshToken에서 이메일 추출
        String email = jwtUtils.getEmailFromToken(refreshToken);

        //모놀리틱으로 주석처리
        // refreshToken 삭제
        //jwtUtils.deleteRefreshToken(email);

        // 내부 메소드를 호출하여 로직 수행 (외부 서비스 호출 대신)
        // 예를 들어, 사용자 인증 및 토큰 재발급 로직
        // 이 부분은 실제 사용자 인증 로직에 따라 다를 수 있음
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email); // 사용자 정보 로드


        String newAccessToken = jwtUtils.createAccessToken(userDetails); // 새 액세스 토큰 생성
        String newRefreshToken = jwtUtils.createRefreshToken(userDetails); // 새 리프레시 토큰 생성

        // 토큰 반환
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);

        return ResponseEntity.ok(tokens);

    }

     */


    // 구글 로그인
    // 구글 로그인 처리
    @PostMapping("/google/login")
    public ResponseEntity<?> authenticateGoogleUser(@RequestBody GoogleRequestDto requestDto) {
        GoogleResponseDto responseDto = authService.authenticateGoogleUser(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    /* 임시 기능정지
    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody MemberDto memberDto) {
        MemberDto result = authService.createMember(memberDto);
        return ResponseEntity.ok(result);
    }

     */

    /* 임시 기능 정지
    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody RequestLoginVo requestLoginVo) {
        return authService.login(requestLoginVo);
    }

     */


    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader(value="Authorization") String accessToken) {
        accessToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        authService.logout(accessToken);
        return ResponseEntity.ok().build();
    }

    // 회원 정보 가져오기
    @GetMapping("/member")
    public ResponseEntity<?> getMemberInfo(@RequestHeader(value="Authorization") String accessToken) {
        accessToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        MembersInfoDto membersInfoDto = authService.getMember(accessToken);
        return ResponseEntity.ok(membersInfoDto);
    }

    // 회원 정보 업데이트
    @PatchMapping("/member")
    public ResponseEntity<?> updateMemberInfo(@RequestHeader(value="Authorization") String accessToken,
                                              @Valid @RequestBody MembersInfoDto membersInfoDto) {
        accessToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        authService.patchMember(accessToken, membersInfoDto);
        return ResponseEntity.ok().build();
    }
}
