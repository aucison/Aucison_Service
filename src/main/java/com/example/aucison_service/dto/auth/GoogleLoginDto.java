package com.example.aucison_service.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Builder
public class GoogleLoginDto {
    private String email;
    private String name;

}
