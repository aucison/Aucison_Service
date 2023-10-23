package com.example.aucison_service.jpa.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "members")
public class Members { // 사용자

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "members_id")
//    private Long id; // 사용자 id

    @Id
    @Column
    private String email; // 구글 이메일

    @Column(nullable = false)
    private String name; // 이름(구글)

    @Column(nullable = false)
    private String nickname; // 별명(사용자 지정값)

    @OneToOne(mappedBy = "members", fetch = FetchType.LAZY) // 양방향 매핑
    private MembersInfo membersInfo;

    @OneToMany(mappedBy = "members", fetch = FetchType.LAZY) // 양방향 매핑
    private List<Wishes> wishes;

    public Members updateInfo(MembersInfoDto membersInfoDto) {
        this.name = membersInfoDto.getName();
        this.nickname = membersInfoDto.getNickName();

        return this;
    }
}
