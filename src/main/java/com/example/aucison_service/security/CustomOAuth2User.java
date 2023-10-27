package com.example.aucison_service.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User, UserDetails {
    private final List<GrantedAuthority> authorities;
    private final Map<String, Object> attributes;
    private final String username;

    public CustomOAuth2User(List<GrantedAuthority> authorities, Map<String, Object> attributes, String username) {
        this.authorities = authorities;
        this.attributes = attributes;
        this.username = username;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return username; // OAuth2User의 name은 사용자의 unique principal name 혹은 "sub"입니다.
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // UserDetails 메서드 구현
    @Override
    public String getPassword() {
        return null; // OAuth2에서는 사용자의 비밀번호를 직접 다루지 않습니다.
    }

    @Override
    public String getUsername() {
        return username; // 일반적으로 이메일 또는 사용자의 고유한 식별자입니다.
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정이 만료되지 않았음을 나타냅니다.
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정이 잠겨 있지 않음을 나타냅니다.
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명이 만료되지 않았음을 나타냅니다.
    }

    @Override
    public boolean isEnabled() {
        return true; // 사용자 계정이 활성화되어 있음을 나타냅니다.
    }
}