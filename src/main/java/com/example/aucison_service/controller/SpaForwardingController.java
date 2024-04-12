package com.example.aucison_service.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardingController {

    // 모든 경로를 처리하는 핸들러 메소드
    @GetMapping("/**")
    public String forward() {
        // 리액트 애플리케이션의 index.html로 포워드
        return "forward:/index.html";
    }
}

