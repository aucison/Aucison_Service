package com.example.aucison_service.service.member;


import com.example.aucison_service.dto.auth.*;
import com.example.aucison_service.jpa.member.MembersEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;

public interface AuthService extends UserDetailsService {


<<<<<<< HEAD
//    GoogleResponseDto authenticateGoogleUser(GoogleRequestDto requestDto);
//    void addTokenToBlacklist(String token);
//    public boolean isTokenBlacklisted(String token);
//    MembersInfoDto getMemberInfo(String email);
//    void updateMemberInfo(String email, MemberUpdateDto updateDto);
=======
    GoogleResponseDto authenticateGoogleUser(GoogleRequestDto requestDto);

//    GoogleResponseDto authenticateGoogleUser(String idToken);

    void addTokenToBlacklist(String token);
    public boolean isTokenBlacklisted(String token);
    MembersInfoDto getMemberInfo(String email);
    void updateMemberInfo(String email, MemberUpdateDto updateDto);
>>>>>>> 2e63f1b1f26e4ab9eb36edde5b8afffb3a65c5cd


    //MemberDto createMember(MemberDto memberDto);
    //ResponseEntity login(RequestLoginVo requestLoginVo);
    //void logout(String accessToken);
    //ResponseEntity reissueToken(String refreshToken);
    //MembersInfoDto getMember(String accessToken);
    //void patchMember(String accessToken, MembersInfoDto membersInfoDto);
    //Iterable<MembersEntity> getMemberByAll();
    //MemberDto getMemberDetailsByGoogleEmail(String email);
}
