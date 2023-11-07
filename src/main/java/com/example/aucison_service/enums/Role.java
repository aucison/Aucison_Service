package com.example.aucison_service.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_CUSTOMER("ROLE_CUSTOMER"),
    ROLE_SELLER("ROLE_SELLER"),
    ROLE_ADMIN("ROLE_ADMIN");

    String role;

    Role(String role) {
        this.role = role;
    }

    public String value() {
        return role;
    }

    @Override
    public String getAuthority() {
        return this.role; // 이 부분 역할 문자열을 반환하도록 수정
    }
}






