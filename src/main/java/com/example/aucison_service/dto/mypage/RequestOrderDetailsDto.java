package com.example.aucison_service.dto.mypage;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RequestOrderDetailsDto {
    private String email;
    private Long ordersId;
    private Long historiesId;
}
