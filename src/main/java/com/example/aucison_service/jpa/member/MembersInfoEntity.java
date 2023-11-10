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
public class MembersInfoEntity { // 사용자 추가 정보

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

    @OneToOne(mappedBy = "membersInfoEntity", fetch = FetchType.LAZY) // 양방향 매핑
    private MembersImgEntity membersImgEntity;

    @OneToMany(mappedBy = "membersInfoEntity", fetch = FetchType.LAZY) // 양방향 매핑
    private List<AddressesEntity> addressesEntityList;

    @OneToMany(mappedBy = "membersInfoEntity", fetch = FetchType.LAZY) // 양방향 매핑
    private List<HistoriesEntity> historiesEntityList;

    public MembersInfoEntity updateInfo(MembersEntity membersEntity, MembersInfoDto membersInfoDto) {
        this.phone = membersInfoDto.getPhone();
        this.subEmail = membersInfoDto.getSubEmail();
        this.membersEntity = membersEntity;

        return this;
    }

    public void updatePhone(String phone) {
        if (phone != null && !phone.isEmpty()) {
            this.phone = phone;
        }
    }

    public void updateCredit(Float newCredit) {
        if (newCredit != null && newCredit >= 0) { // 유효성 검사, credit이 null이 아니고 음수가 아니어야 함
            this.credit = newCredit;
        }
    }
}
