package com.example.aucison_service.service.member;

import com.example.aucison_service.jpa.member.MembersEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Collections;

public class MemberDetails extends User {

    private MembersEntity members;

    public MemberDetails(MembersEntity members) {
        super(members.getEmail(), "", getAuthorities(members));
        this.members = members;
    }

    public MembersEntity getMember() {
        return members;
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(MembersEntity member) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()));
    }
}

