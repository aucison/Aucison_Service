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
    //MemberDto createMember(MemberDto memberDto);
    //ResponseEntity login(RequestLoginVo requestLoginVo);
  //  void logout(String accessToken);
//    ResponseEntity reissueToken(String refreshToken);
  //  MembersInfoDto getMember(String accessToken);
  //  void patchMember(String accessToken, MembersInfoDto membersInfoDto);
    GoogleResponseDto authenticateGoogleUser(GoogleRequestDto requestDto);

  //  Iterable<MembersEntity> getMemberByAll();
//    MemberDto getMemberDetailsByGoogleEmail(String email);
}
