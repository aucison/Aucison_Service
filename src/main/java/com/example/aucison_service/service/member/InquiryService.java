package com.example.aucison_service.service.member;

import com.example.aucison_service.dto.inquiry.InquiryRequestDto;
import com.example.aucison_service.dto.inquiry.InquiryResponseDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public interface InquiryService {

    InquiryResponseDto registInquiryNorm(InquiryRequestDto dto,  @AuthenticationPrincipal MemberDetails principal);

    InquiryResponseDto registDeclare(InquiryRequestDto dto,  @AuthenticationPrincipal MemberDetails principal);
}
