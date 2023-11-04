package com.example.aucison_service.jpa.member;

import com.example.aucison_service.dto.auth.MembersInfoDto;
import com.example.aucison_service.enums.Role;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.util.List;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "members")
public class MembersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "members_id")
    private Long id;

    @Column(nullable = false)
    private String email;   //구글 이메일

    @Column(nullable = false)
    private String name;    //구글 이름

    @Column(nullable = true)
    private String nickname;    //닉네임

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "membersEntity", fetch = FetchType.LAZY)
    private MembersInfo membersInfo;

    @OneToMany(mappedBy = "membersEntity", fetch = FetchType.LAZY)
    private List<Wishes> wishes;

    @Builder
    public MembersEntity(String email, String name, String nickname, Role role) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.role = role;
    }

    public void updateFromGoogle(GoogleIdToken.Payload payload) {
        this.email = payload.getEmail();
        this.name = (String) payload.get("name");
        this.nickname = this.name + "_google";
    }

    public void updateInfo(MembersInfoDto membersInfoDto) {
        this.name = membersInfoDto.getName();
        this.nickname = membersInfoDto.getNickName();
    }

    public void updateNickname(String nickname) {
        if (nickname != null && !nickname.isEmpty()) {
            this.nickname = nickname;
        }
    }
}
