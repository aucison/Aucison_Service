package com.example.aucison_service.jpa.member;

import com.example.aucison_service.dto.auth.MembersInfoDto;
import com.example.aucison_service.enums.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.util.List;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "members")
public class MembersEntity { // 사용자

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "members_id")
    private Long id; // 사용자 id

    @Column
    private String email; // 구글 이메일

    @Column(nullable = false)
    private String name; // 이름(구글)

    @Column(nullable = false)
    private String nickname; // 별명(사용자 지정값)

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "membersEntity", fetch = FetchType.LAZY) // 양방향 매핑
    private MembersInfo membersInfo;

    @OneToMany(mappedBy = "membersEntity", fetch = FetchType.LAZY) // 양방향 매핑
    private List<Wishes> wishes;


    @Builder
    public MembersEntity(String email, String name, String nickname) {
        this.name = name;
        this.email = email;
        this.nickname = nickname;
    }

    public MembersEntity updateInfo(MembersInfoDto membersInfoDto) {
        this.name = membersInfoDto.getName();
        this.nickname = membersInfoDto.getNickName();
        return this;
    }

    public void updateGoogleInfo(String name, String email) {
        this.name = name;
        this.email = email;
        // 필요하다면 다른 정보도 업데이트 할 수 있습니다.
    }

    public void updateEmail(String email) {
        this.email = email;
        // 필요한 경우 다른 값들도 여기에서 업데이트할 수 있습니다.
    }

}
