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
@Table(name = "members_info")
public class MembersInfo { // 사용자 추가 정보

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "members_info_id")
    private Long id; // 사용자 추가 정보 id

    @Column(nullable = false)
    private String phone; // 전화번호

    @Column(nullable = false)
    private Float credit; // 사용자 자산

    @Column(name = "sub_email")
    private String subEmail; // 수신 이메일

    @OneToOne
    @JoinColumn(name = "email") // 연관관계 주인
    private Members members; // 사용자

    @OneToOne(mappedBy = "membersInfoEntity", fetch = FetchType.LAZY) // 양방향 매핑
    private MembersImgEntity membersImgEntity;

    @OneToMany(mappedBy = "membersInfoEntity", fetch = FetchType.LAZY) // 양방향 매핑
    private List<AddressesEntity> addressesEntity;

    @OneToMany(mappedBy = "membersInfoEntity", fetch = FetchType.LAZY) // 양방향 매핑
    private List<HistoriesEntity> historiesEntity;

    public MembersInfo updateInfo(Members members, MembersInfoDto membersInfoDto) {
        this.phone = membersInfoDto.getPhone();
        this.subEmail = membersInfoDto.getSubEmail();
        this.members = members;

        return this;
    }
}
