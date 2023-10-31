package com.example.aucison_service.jpa.member;

import com.example.aucison_service.dto.auth.MembersInfoDto;
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
@Table(name = "members_info")
public class MembersInfo { // 사용자 추가 정보

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "members_info_id")
    private Long id; // 사용자 추가 정보 id

    @Column(nullable = true)
    private String phone; // 전화번호

    @Column(nullable = false)
    private Float credit; // 사용자 자산

    @Column(name = "sub_email")
    private String subEmail; // 수신 이메일

    @OneToOne
    @JoinColumn(name = "email") // 연관관계 주인
    private MembersEntity membersEntity; // 사용자

    @OneToOne(mappedBy = "membersInfo", fetch = FetchType.LAZY) // 양방향 매핑
    private MembersImg membersImg;

    @OneToMany(mappedBy = "membersInfo", fetch = FetchType.LAZY) // 양방향 매핑
    private List<Addresses> addressesList;

    @OneToMany(mappedBy = "membersInfo", fetch = FetchType.LAZY) // 양방향 매핑
    private List<Histories> historiesList;

    public MembersInfo updateInfo(MembersEntity membersEntity, MembersInfoDto membersInfoDto) {
        this.phone = membersInfoDto.getPhone();
        this.subEmail = membersInfoDto.getSubEmail();
        this.membersEntity = membersEntity;

        return this;
    }
}
