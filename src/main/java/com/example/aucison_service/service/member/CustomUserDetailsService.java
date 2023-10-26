package com.example.aucison_service.service.member;

import com.example.aucison_service.jpa.member.MembersEntity;
import com.example.aucison_service.jpa.member.MembersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final MembersRepository membersRepository;

    @Autowired
    public CustomUserDetailsService(MembersRepository membersRepository) {
        this.membersRepository = membersRepository;
    }

    //사용자의 이메일로 사용자 정보를 로드한다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MembersEntity user = membersRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }


        // 사용자의 권한을 GrantedAuthority로 변환.
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getAuthority());

        // authorities 리스트 생성 -> 다른 권한이 있다면 리스트에 추가할 수 있음
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);

        // 비밀번호 필드가 없기 때문에 UserDetails 객체를 생성할 때 임의의 값을 사용
        // 실제 시나리오에서는 사용자의 비밀번호를 인코딩하여 저장하므로 수정해야함
        return new User(user.getEmail(), "", authorities);
    }
}