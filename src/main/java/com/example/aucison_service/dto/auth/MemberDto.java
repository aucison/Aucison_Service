package com.example.aucison_service.dto.auth;

import com.example.aucison_service.jpa.member.Members;
import com.example.aucison_service.vo.RequestSignInVo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberDto {
//    private Long id;
    private String email;
    private String name;
    private String nickname;

    // DTO -> Entity
    public Members toEntity() {
        return Members.builder()
                .email(this.email)
                .name(this.name)
                .nickname(this.nickname)
                .build();
    }

    // Entity -> DTO
    public static MemberDto fromEntity(Members members) {
        return MemberDto.builder()
                .email(members.getEmail())
                .name(members.getName())
                .nickname(members.getNickname())
                .build();
    }

    // RequestVO -> DTO
    public MemberDto(RequestSignInVo request) {
        this.email = request.getEmail();
        this.name = request.getName();
        this.nickname = request.getNickname();
    }

}
