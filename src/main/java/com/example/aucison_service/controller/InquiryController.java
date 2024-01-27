package com.example.aucison_service.controller;


import com.example.aucison_service.dto.ApiResponse;
import com.example.aucison_service.dto.board.PostCRUDResponseDto;
import com.example.aucison_service.dto.board.PostRegistRequestDto;
import com.example.aucison_service.dto.inquiry.InquiryRequestDto;
import com.example.aucison_service.dto.inquiry.InquiryResponseDto;
import com.example.aucison_service.service.member.InquiryService;
import com.example.aucison_service.service.member.MemberDetails;
import com.example.aucison_service.service.product.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/help")
public class InquiryController {
    private InquiryService inquiryService;

    @Autowired
    public InquiryController(InquiryService inquiryService){
        this.inquiryService=inquiryService;
    }

    @PostMapping("/inquiry")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<InquiryResponseDto> registInquiryNorm(
            @RequestBody InquiryRequestDto inquiryRequestDto,
            @AuthenticationPrincipal MemberDetails principal) {

        return ApiResponse.createSuccess(inquiryService.registInquiryNorm( inquiryRequestDto, principal));
    }

    @PostMapping("/declare")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<InquiryResponseDto> registDeclare(
            @RequestBody InquiryRequestDto inquiryRequestDto,
            @AuthenticationPrincipal MemberDetails principal) {

        return ApiResponse.createSuccess(inquiryService.registDeclare( inquiryRequestDto, principal));
    }
}
