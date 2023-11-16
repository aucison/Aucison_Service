package com.example.aucison_service.dto.board;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PostRegistRequestDto {

    private String title;               // 제목
    private String content;             //내용


    //게시글 등록자 누군지 알아야 함
    private String email;
}
