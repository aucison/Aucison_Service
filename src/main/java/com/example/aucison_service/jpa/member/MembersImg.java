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
public class MembersImg { // 사용자 프로필 사진

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "members_imgs_id")
    private Long id; // 사용자 프로필 사진 id

    @Column(nullable = false)
    private String name; // 이미지 이름

    @Column(nullable = false)
    private String path; // 저장 위치

    @Column(nullable = false)
    private String url; // 등록 url

    @Column(nullable = false)
    private String type; // 파일 확장자

    @Column(nullable = false)
    // type 수정이 필요합니다.
    private String size; // 파일 크기

    @Column(name = "h_w", nullable = false)
    // type 수정이 필요합니다.
    private String hW; // 이미지 가로세로 길이

    @Column(name = "created_at", nullable = false)
    // type 수정이 필요합니다.
    private Date createdAt; // 이미지 생성일

    @OneToOne
    @JoinColumn(name = "members_info_id") // 연관관계 주인
    private MembersInfo membersInfo; // 사용자 추가 정보

    public void updateInfo(MembersInfo membersInfo, MembersInfoDto membersInfoDto) {
        this.url = membersInfoDto.getImgUrl();
        this.membersInfo = membersInfo;
    }
}
