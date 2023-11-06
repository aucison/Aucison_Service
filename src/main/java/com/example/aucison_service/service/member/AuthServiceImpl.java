package com.example.aucison_service.service.member;


import com.example.aucison_service.dto.auth.*;
import com.example.aucison_service.jpa.member.*;
import com.example.aucison_service.util.JwtUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final MembersRepository membersRepository;
    private final MembersInfoRepository membersInfoRepository;
    private final MembersImgRepository membersImgRepository;
    private final JwtUtils jwtUtils;
    private final GoogleService googleService;
    private final Set<String> tokenBlacklist = Collections.synchronizedSet(new HashSet<>());


//    //구글 로그인 처리
    @Override
    @Transactional
    public GoogleResponseDto authenticateGoogleUser(GoogleRequestDto requestDto) {
        GoogleIdToken.Payload payload = googleService.verify(requestDto.getIdToken());

        // Google payload에서 이메일을 기반으로 사용자 검색 또는 생성
        MembersEntity user = membersRepository.findByEmail(payload.getEmail());

        if (user == null) {
            user = new MembersEntity();
            user.updateFromGoogle(payload);
            membersRepository.save(user);
        }


        // 사용자에 대한 JWT 토큰 생성
        GoogleLoginDto googleLoginDto = GoogleLoginDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .build();

        // JWT 토큰 생성에 필요한 정보가 있다면 GoogleLoginDto에서 가져옴
        String accessToken = jwtUtils.createAccessToken(googleLoginDto);
        String refreshToken = jwtUtils.createRefreshToken(googleLoginDto);

        return GoogleResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

//    @Override
//    public GoogleResponseDto authenticateGoogleUser(GoogleRequestDto requestDto) {
//        GoogleIdToken.Payload payload = googleService.verify(requestDto.getIdToken());
//
//        // Google payload에서 이메일을 기반으로 사용자 검색 또는 생성
//        MembersEntity user = membersRepository.findByEmail(payload.getEmail());
//
//        if (user == null) {
//            user = new MembersEntity();
//            user.updateFromGoogle(payload);
//            membersRepository.save(user);
//        }
//
//        // 사용자에 대한 JWT 토큰 생성
//        GoogleLoginDto googleLoginDto = GoogleLoginDto.builder()
//                .email(user.getEmail())
//                .name(user.getName())
//                .build();
//
//        // JWT 토큰 생성에 필요한 정보가 있다면 GoogleLoginDto에서 가져옴
//        String accessToken = jwtUtils.createAccessToken(googleLoginDto);
//        String refreshToken = jwtUtils.createRefreshToken(googleLoginDto);
//
//        return GoogleResponseDto.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .build();
//    }

    //로그아웃 관련 블랙리스트
    public void addTokenToBlacklist(String token) {
        tokenBlacklist.add(token);
    }

    //로그아웃 관련 블랙리스트
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }



    //회원 상세정보 조회 -> 현재 이미지, 배송지 안됨
    public MembersInfoDto getMemberInfo(String email) {
        MembersEntity member = membersRepository.findByEmail(email);
        if (member == null) {
            throw new RuntimeException("User not found");
        }

        MembersInfo membersInfo = member.getMembersInfo();  //1대 1 관계라 가능
        if (membersInfo == null) {
            throw new RuntimeException("MemberInfo not found for this user");
        }
        return MembersInfoDto.builder()
                .email(member.getEmail())
                .name(member.getName())
                .nickName(member.getNickname())
                .phone(membersInfo.getPhone())
                .build();
    }

    //회원정보 수정 -> 현재 이미지, 배송지 안됨
    @Transactional
    public void updateMemberInfo(String email, MemberUpdateDto updateDto) {
        MembersEntity member = membersRepository.findByEmail(email);
        if (member == null) {
            throw new RuntimeException("User not found");
        }

        // nickname이 존재하면 수정
        member.updateNickname(updateDto.getNickname());

        // phone이 존재하면 수정
        member.getMembersInfo().updatePhone(updateDto.getPhone());
    }


    /* 임시로 구글로그인만 활성화
    @Override
    public MemberDto createMember(MemberDto memberDto) {

        // 이메일 중복 검사
        if (membersRepository.existsByEmail(memberDto.getEmail())) {
            throw new RuntimeException("Error: 중복된 이메일");
        }
        MembersEntity membersEntity = MembersEntity.builder()
                .email(memberDto.getEmail())
                .name(memberDto.getName())
                .build();

        membersRepository.save(membersEntity);

        return new ModelMapper().map(membersEntity, MemberDto.class);
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

     */


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }


}
