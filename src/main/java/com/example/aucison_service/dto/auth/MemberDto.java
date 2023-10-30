package com.example.aucison_service.dto.auth;

import com.example.aucison_service.jpa.member.MembersEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberDto {
    private String email;
    private String name;
    private String nickname;

    // DTO -> Entity
    public MembersEntity toEntity() {
        return MembersEntity.builder()
                .email(this.email)
                .name(this.name)
                .nickname(this.nickname)
                .build();
    }

    // Entity -> DTO
    public static MemberDto fromEntity(MembersEntity members) {
        return MemberDto.builder()
                .email(members.getEmail())
                .name(members.getName())
                .nickname(members.getNickname())
                .build();
    }


}
