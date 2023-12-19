package com.example.aucison_service.service.member;

import com.example.aucison_service.dto.customlogin.LoginRequestDto;
import com.example.aucison_service.dto.customlogin.LoginResponseDto;
import com.example.aucison_service.dto.customlogin.SigninRequestDto;
import com.example.aucison_service.dto.customlogin.SigninResponseDto;
import com.example.aucison_service.enums.Role;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.member.MembersEntity;
import com.example.aucison_service.jpa.member.MembersRepository;
import com.example.aucison_service.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MemberService {

    @Autowired
    private MembersRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    //회원가입
    public SigninResponseDto registerUser(SigninRequestDto signinRequestDto) {
        if (memberRepository.existsByEmail(signinRequestDto.getEmail())) {
            throw new AppException(ErrorCode.DUPLICATE_EMAIL); // 이메일 중복 체크
        }

        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(signinRequestDto.getPassword());

        // 회원 정보 저장
        MembersEntity member = MembersEntity.builder()
                .email(signinRequestDto.getEmail())
                .password(encryptedPassword)
                .nickname(signinRequestDto.getNickname())
                .role(Role.ROLE_CUSTOMER) // 기본 역할 설정
                .build();

        MembersEntity savedMember = memberRepository.save(member);

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(member.getEmail(), member.getRole());

        return SigninResponseDto.builder()
                .userId(savedMember.getId())
                .email(savedMember.getEmail())
                .token(token)
                .build();
    }

    //로그인
    public LoginResponseDto loginUser(LoginRequestDto loginRequestDto) {
        MembersEntity member = memberRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_EMAIL));    //이메일 존재하지 않음

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new AppException(ErrorCode.NOT_MATCH_PW); // 비밀번호 검증
        }

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(member.getEmail(), member.getRole());

        return LoginResponseDto.builder()
                .userId(member.getId())
                .email(member.getEmail())
                .token(token)
                .build();
    }


}
