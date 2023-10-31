package com.example.aucison_service.service.member;


import com.example.aucison_service.dto.auth.GoogleRequestDto;
import com.example.aucison_service.dto.auth.GoogleResponseDto;
import com.example.aucison_service.dto.auth.MemberDto;
import com.example.aucison_service.dto.auth.MembersInfoDto;
import com.example.aucison_service.jpa.member.MembersEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;

public interface AuthService extends UserDetailsService {


    GoogleResponseDto authenticateGoogleUser(GoogleRequestDto requestDto);
    void addTokenToBlacklist(String token);
    public boolean isTokenBlacklisted(String token);
    MembersInfoDto getMemberInfo(String email);


    //MemberDto createMember(MemberDto memberDto);
    //ResponseEntity login(RequestLoginVo requestLoginVo);
    //void logout(String accessToken);
    //ResponseEntity reissueToken(String refreshToken);
    //MembersInfoDto getMember(String accessToken);
    //void patchMember(String accessToken, MembersInfoDto membersInfoDto);
    //Iterable<MembersEntity> getMemberByAll();
    //MemberDto getMemberDetailsByGoogleEmail(String email);
}
