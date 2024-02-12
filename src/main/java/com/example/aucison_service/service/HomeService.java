package com.example.aucison_service.service;


import com.example.aucison_service.dto.home.HomeResponseDto;
import com.example.aucison_service.service.member.MemberDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public interface HomeService {
    HomeResponseDto getHomeData(@AuthenticationPrincipal MemberDetails principal);
}
