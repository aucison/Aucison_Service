package com.example.aucison_service.controller;

import com.example.aucison_service.dto.ApiResponse;
import com.example.aucison_service.service.hidden.CreditService;
import com.example.aucison_service.service.member.MemberDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hidden/credit")
public class CreditController {
    private final CreditService creditService;

    @Autowired
    public CreditController(CreditService creditService) {
        this.creditService = creditService;
    }

    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<?> updateCredit(@AuthenticationPrincipal MemberDetails principal,
                                            @RequestParam float changeCredit) {
        return ApiResponse.createSuccessWithNoData("Updated credit: " + creditService.updateCredit(principal,changeCredit));
    }
}
