package com.example.aucison_service.dto.board;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUpdateRequestDto {
    private String title;
    private String content;
}
