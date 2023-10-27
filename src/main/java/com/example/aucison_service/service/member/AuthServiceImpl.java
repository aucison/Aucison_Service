package com.example.aucison_service.service.member;


import com.example.aucison_service.dto.auth.MemberDto;
import com.example.aucison_service.dto.auth.MembersInfoDto;
import com.example.aucison_service.jpa.member.*;
import com.example.aucison_service.util.JwtUtils;
import com.example.aucison_service.vo.RequestLoginVo;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final MembersRepository membersRepository;
    private final MembersInfoRepository membersInfoRepository;
    private final MembersImgRepository membersImgRepository;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;




    /* 해당방식은 구글 방식이 아님

    @Override
    public MemberDto createMember(MemberDto memberDto) {
        // 이메일 중복 검사
        if (membersRepository.existsByEmail(memberDto.getEmail())) {
            throw new RuntimeException("Error: 중복된 이메일");
        }
        MembersEntity membersEntity = MembersEntity.builder()
                .email(memberDto.getEmail())
                .name(memberDto.getName())
//                        .nickname(memberDto.getNickname())
                .build();

        membersRepository.save(membersEntity);

        return new ModelMapper().map(membersEntity, MemberDto.class);
    }

     */


    private final BCryptPasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 컴포넌트

    // 토큰의 무결성을 검증하기 위한 메소드 추가
    private boolean isTokenIntegrityValid(String token, MemberDto member) {
        // 실제 구현에서는 토큰의 서명을 검증하고, 토큰의 payload에 있는 사용자 정보와 실제 사용자의 정보가 일치하는지 확인합니다.
        // 이 과정은 토큰이 변경되거나 변조되지 않았는지 확인하는 데 필요합니다.
        // JWT 라이브러리의 기능을 사용하여 구현할 수 있습니다.

        // 예시 (실제 코드는 JWT 라이브러리에 따라 다를 수 있음):
        try {
            // 토큰 서명 검증
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey) // 시크릿 키는 별도로 관리되어야 합니다.
                    .build()
                    .parseClaimsJws(token);

            // 클레임에서 사용자 정보 가져오기
            String tokenEmail = claims.getBody().getSubject();

            // 토큰의 사용자 정보와 실제 사용자 정보 비교
            return tokenEmail.equals(member.getEmail());
        } catch (JwtException e) {
            return false; // 토큰 검증 실패 시
        }
    }

    // 요청의 출처를 확인하는 메소드 추가
    private boolean isRequestOriginValid(String origin) {
        // 실제 구현에서는 요청의 출처를 확인하여, 알려진 및 신뢰할 수 있는 출처의 요청만을 수락합니다.
        // 이 과정은 CSRF 공격 및 기타 웹 보안 위협으로부터 시스템을 보호하는 데 도움이 됩니다.

        // 예시:
        List<String> trustedOrigins = Arrays.asList("https://trusteddomain.com"); // 신뢰할 수 있는 도메인 리스트
        return trustedOrigins.contains(origin); // 요청 출처가 신뢰할 수 있는 목록에 있는지 확인
    }

    @Override
    public MemberDto createMember(MemberDto memberDto) {
        // ... [기존 코드]

        // 비밀번호 암호화 처리
        String encryptedPassword = passwordEncoder.encode(memberDto.getPassword());
        memberDto.setPassword(encryptedPassword);

        // ... [나머지 코드]
    }

    @Override
    public ResponseEntity<?> login(RequestLoginVo requestLoginVo) {
        // ... [기존 로그인 처리 코드]

        // 토큰의 무결성 및 요청의 출처 검증
        if (!isTokenIntegrityValid(accessToken, memberDto) || !isRequestOriginValid(request.getHeader("Origin"))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or untrusted origin");
        }

        // ... [나머지 코드]
    }


    @Override
    public MemberDto createMember(MemberDto memberDto) {
        // Google OAuth 토큰을 검증합니다.
        GoogleIdToken.Payload payload;
        try {
            payload = verifyGoogleToken(memberDto.getOauthToken());
        } catch (Exception e) {
            throw new RuntimeException("Google Token verification failed", e);
        }

        // 검증된 토큰에서 사용자 정보를 추출합니다.
        String email = payload.getEmail();
        String name = (String) payload.get("name");  // Google의 응답에서 이름을 얻습니다.

        // 이미 존재하는 사용자인지 확인합니다.
        MembersEntity existingMember = membersRepository.findByEmail(email);
        if (existingMember != null) {
            throw new RuntimeException("User already exists with email: " + email);
        }

        // 새 멤버 생성
        MembersEntity membersEntity = MembersEntity.builder()
                .email(email)
                .name(name)
                // 추가 정보 설정 (닉네임 등)
                .build();

        // 데이터베이스에 사용자 정보를 저장합니다.
        membersRepository.save(membersEntity);

        // Dto로 변환하여 반환합니다.
        return new ModelMapper().map(membersEntity, MemberDto.class);
    }

    // Google 토큰을 검증하는 메서드입니다.
    private GoogleIdToken.Payload verifyGoogleToken(String idTokenString) throws Exception {
        // (Google API 클라이언트 ID를 사용하여 초기화된) GoogleIdTokenVerifier 객체를 이용하여 토큰을 검증합니다.
        GoogleIdTokenVerifier verifier = /* ... */;
        GoogleIdToken idToken = verifier.verify(idTokenString);

        if (idToken != null) {
            return idToken.getPayload();
        } else {
            throw new IllegalArgumentException("Invalid ID token.");
        }
    }

    @Override
    public ResponseEntity login(RequestLoginVo requestLoginVo) {
        MembersEntity membersEntity = membersRepository.findByEmail(requestLoginVo.getEmail());

        if (membersEntity == null) {
            // 이메일로 사용자를 찾을 수 없는 경우, 명확한 예외 메시지와 함께 예외를 발생
            throw new UsernameNotFoundException("User not found with email: " + requestLoginVo.getEmail());
        }

        // DTO 변환(MembersEntity -> MemberDto)
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        MemberDto memberDto = mapper.map(membersEntity, MemberDto.class);

        // JWT 토큰 생성
        String accessToken = jwtUtils.createAccessToken(memberDto);
        String refreshToken = jwtUtils.createRefreshToken(memberDto);

        // 응답 헤더에 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("RefreshToken", refreshToken);

        // 응답 본문에도 토큰 정보를 넣을 수 있음 (선택사항)
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return ResponseEntity.ok()
                .headers(headers) // 헤더에 토큰을 포함시킬 수 있음
                .body(tokens); // 응답 본문에 토큰을 넣음, 이 부분은 선택사항
    }

    //현재 미구현으로 두는게 나음, 그 이유는 블랙리스트관련 서비스를 아직 만들지 않음 -> 결국에는 만들어야하나 원할한 테스트를 위해 주석 하는게 나아보임
    @Override
    public void logout(String accessToken) {
        // 토큰에서 이메일 추출
        String email = jwtUtils.getEmailFromToken(accessToken);

        if(email == null) {
            throw new RuntimeException("Could not find email in the token");
        }

        // refreshToken 무효화 (DB에서 삭제 등)
        jwtUtils.deleteRefreshToken(email);

        // accessToken을 블랙리스트에 추가하여 더 이상 사용할 수 없도록 함
        jwtUtils.setBlackList(accessToken);
    }



    //이 아래로 아직 미수정!!!!
    //이 아래로 아직 미수정!!!!
    @Override
    public MembersInfoDto getMember(String accessToken) {
        String email = jwtUtils.getEmailFromToken(accessToken);
        if (email != null) {
            // null 검증 로직 추가하기
            MembersEntity membersEntity = membersRepository.findByEmail(email);
            MembersInfo membersInfo = membersInfoRepository.findByMembers(membersEntity);
            MembersImg membersImgEntity = membersImgRepository.findByMembersInfo(membersInfo);

            return MembersInfoDto.builder()
                    .subEmail(membersInfo.getSubEmail())
                    .name(membersEntity.getName())
                    .nickName(membersEntity.getNickname())
                    .phone(membersInfo.getPhone())
                    .imgUrl(membersImgEntity.getUrl())
                    .build();
        } else {
            throw new RuntimeException();
        }
    }

    @Transactional
    @Override
    public void patchMember(String accessToken, MembersInfoDto membersInfoDto) {
        String email = jwtUtils.getEmailFromToken(accessToken);
        MembersEntity membersEntity = membersRepository.findByEmail(email);
        MembersInfo membersInfo = membersInfoRepository.findByMembers(membersEntity);
        MembersImg membersImg = membersImgRepository.findByMembersInfo(membersInfo);

        membersImg.updateInfo(
                membersInfo.updateInfo(
                        membersEntity.updateInfo(membersInfoDto),
                        membersInfoDto),
                membersInfoDto);
    }

    @Override
    public Iterable<MembersEntity> getMemberByAll() {
        return null;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
