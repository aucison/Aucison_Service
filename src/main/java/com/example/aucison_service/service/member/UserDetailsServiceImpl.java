package com.example.aucison_service.service.member;

import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.member.entity.MembersEntity;
import com.example.aucison_service.jpa.member.repository.MembersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private MembersRepository membersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MembersEntity member = membersRepository.findByEmail(username);
        if (member == null) {
            throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
        }
//        return new User(member.getEmail(), "", getAuthorities(member)); // 빈 비밀번호 사용
        return new MemberDetails(member); // MemberDetails 객체를 반환
    }

//    // 추가 메소드: 권한 정보를 설정합니다
//    private Collection<? extends GrantedAuthority> getAuthorities(MembersEntity member) {
//        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()));
//    }

}