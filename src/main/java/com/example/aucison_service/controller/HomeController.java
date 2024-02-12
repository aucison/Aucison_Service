package com.example.aucison_service.controller;

import com.example.aucison_service.dto.ApiResponse;
import com.example.aucison_service.dto.home.HomeResponseDto;
import com.example.aucison_service.service.HomeService;
import com.example.aucison_service.service.member.MemberDetails;
import com.example.aucison_service.service.product.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {


    private HomeService homeService;

    @Autowired
    public HomeController(HomeService homeService){
        this.homeService=homeService;
    }


    //인증을 받지 않은 사용자와 인증을 받은 사용자 모두에게 홈 화면이 열려 있고,보여지는 정보의 차이가 닉네임 하나뿐이라면 인증 여부에 따라 접근을 제한하지 않는 것이 적합
    @GetMapping
    public ApiResponse<HomeResponseDto> getHomeData(Authentication authentication) {
        MemberDetails principal = null;
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            principal = (MemberDetails) authentication.getPrincipal();
        }

        HomeResponseDto homeData = homeService.getHomeData(principal);
        return ApiResponse.createSuccess(homeData);
    }




}
