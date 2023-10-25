package com.example.aucison_service.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestLoginVo {
    private String email;
}
