package com.example.aucison_service.service.member;

import com.example.aucison_service.jpa.member.MembersEntity;
import com.example.aucison_service.jpa.member.MembersRepository;
import com.example.aucison_service.security.CustomOAuth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService, OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MembersRepository membersRepository;

    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();

    @Autowired
    public CustomUserDetailsService(MembersRepository membersRepository) {
        this.membersRepository = membersRepository;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MembersEntity user = membersRepository.findByEmail(username);
        return getUserDetails(user);
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        MembersEntity user = membersRepository.findByEmail(email);

        if (user == null) {
            // 여기서는 신규 회원 데이터를 생성하고 저장하는 로직을 작성합니다.
            user = new MembersEntity();
            user.updateEmail(email);
            // 다른 필요한 정보도 설정할 수 있습니다.
            membersRepository.save(user);
        }

        // 이 부분에서 사용자의 권한을 가져와서 authorities에 설정합니다.
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getAuthority()));

        // 이제 CustomOAuth2User 객체를 생성하고 반환합니다.
        return new CustomOAuth2User(authorities, oAuth2User.getAttributes(), user.getEmail());
    }


    private UserDetails getUserDetails(MembersEntity user) {
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