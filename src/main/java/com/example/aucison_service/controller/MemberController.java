package com.example.aucison_service.controller;

import com.example.aucison_service.dto.customlogin.LoginRequestDto;
import com.example.aucison_service.dto.customlogin.LoginResponseDto;
import com.example.aucison_service.dto.customlogin.SigninRequestDto;
import com.example.aucison_service.dto.customlogin.SigninResponseDto;
import com.example.aucison_service.service.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MemberController {

    @Autowired
    private MemberService memberService;


    @PostMapping("/signin")
    public ResponseEntity<?> registerUser(@RequestBody SigninRequestDto signinRequestDto) {
        SigninResponseDto response = memberService.registerUser(signinRequestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto response = memberService.loginUser(loginRequestDto);
        return ResponseEntity.ok(response);
    }


}