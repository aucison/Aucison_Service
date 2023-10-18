package com.example.aucison_service.dto.board;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
public class CommentRegistRequestDto {
    private String content;             //답변
    private LocalDateTime createdTime;            //댓글 등록시간

    //게시글 등록자 누군지 알아야 함
    private String email;
}
