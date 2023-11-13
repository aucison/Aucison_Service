package com.example.aucison_service.jpa.member;


import com.example.aucison_service.dto.auth.MembersInfoDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "members_img")
public class MembersImgEntity { // 사용자 프로필 사진

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "members_imgs_id")
    private Long id; // 사용자 프로필 사진 id

    @Column(nullable = false)
    private String url; // 등록 url

    @OneToOne
    @JoinColumn(name = "members_info_id") // 연관관계 주인
    private MembersInfoEntity membersInfoEntity; // 사용자 추가 정보

    public void updateInfo(MembersInfoEntity membersInfoEntity, MembersInfoDto membersInfoDto) {
        this.url = membersInfoDto.getImgUrl();
        this.membersInfoEntity = membersInfoEntity;
    }
}
