package com.example.aucison_service.dto.board;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class PostListResponseDto {

    private Long posts_id;
    private String title;
    private String content;
    private LocalDateTime createdTime;
    //user-service에서 닉네임 가져오기
    private String email;

    private List<CommentListResponseDto> comments;
}
