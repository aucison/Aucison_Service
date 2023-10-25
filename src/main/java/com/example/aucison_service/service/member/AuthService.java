package com.example.aucison_service.service.member;


import com.example.aucison_service.dto.auth.MemberDto;
import com.example.aucison_service.dto.auth.MembersInfoDto;
import com.example.aucison_service.jpa.member.Members;
import com.example.aucison_service.vo.RequestLoginVo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {
    MemberDto createMember(MemberDto memberDto);
    ResponseEntity login(RequestLoginVo requestLoginVo);
    void logout(String accessToken);
//    ResponseEntity reissueToken(String refreshToken);
    MembersInfoDto getMember(String accessToken);
    void patchMember(String accessToken, MembersInfoDto membersInfoDto);

    Iterable<Members> getMemberByAll();
//    MemberDto getMemberDetailsByGoogleEmail(String email);
}
