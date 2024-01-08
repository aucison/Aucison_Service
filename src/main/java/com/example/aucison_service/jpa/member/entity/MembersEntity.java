package com.example.aucison_service.jpa.member.entity;

import com.example.aucison_service.enums.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.util.List;
import java.util.Map;

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

    private String nickname;    //닉네임

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "membersEntity", fetch = FetchType.LAZY)
    private MembersInfoEntity membersInfoEntity;

    @OneToMany(mappedBy = "membersEntity", fetch = FetchType.LAZY)
    private List<WishesEntity> wishes;

    @Builder
    public MembersEntity(String email, String name, String nickname, Role role) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.role = role;
    }

    public void updateFromGoogle(Map<String, Object> userInfo) {
        this.email = (String) userInfo.get("email");
        this.name = (String) userInfo.get("name");
        //this.nickname = this.name + "_google";
    }

    public void updateNickname(String nickname) {
        if (nickname != null && !nickname.isEmpty()) {
            this.nickname = nickname;
        }
    }
}
