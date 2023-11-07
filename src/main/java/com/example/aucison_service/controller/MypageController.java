<<<<<<< HEAD
package com.example.aucison_service.controller;


import com.example.aucison_service.dto.ApiResponse;
import com.example.aucison_service.dto.mypage.RequestOrderDetailsDto;
import com.example.aucison_service.dto.mypage.ResponseOrderDetailsDto;
import com.example.aucison_service.service.member.MypageService;
import com.example.aucison_service.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/member-service")
public class MypageController {

    private final JwtUtils jwtUtils;
    private final MypageService mypageService;

=======
//package com.example.aucison_service.controller;
//
//
//import com.example.aucison_service.dto.mypage.RequestOrderDetailsDto;
//import com.example.aucison_service.dto.mypage.ResponseOrderDetailsDto;
//import com.example.aucison_service.service.member.MypageService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RequiredArgsConstructor
//@RestController
//@RequestMapping("/")
//public class MypageController {
//
//
//    private final MypageService mypageService;
//
>>>>>>> fa2c890fac06c4700776050349df8dda36a0037a
//    @GetMapping("/mp/buy")
//    public ResponseEntity getOrderInfo(@RequestHeader("accessToken") String accessToken) {
//        String email = jwtUtils.getEmailFromToken(accessToken);
//        return ResponseEntity.status(HttpStatus.OK).body(mypageService.getOrderHistoryList(email));
//    }
<<<<<<< HEAD
    @GetMapping("/mp/buy")  //주문내역 조회
    public ApiResponse<?> getOrderInfo(@RequestParam("email") String email) {  //token 대신 이메일 사용(임시)
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        String email = userDetails.getUsername();

        return ApiResponse.createSuccess(mypageService.getOrderHistoryList(email));
    }

    @GetMapping("/mp/buy/{historiesId}")
    public ResponseEntity getOrderDetail(@PathVariable("historiesId") Long historiesId,
                                         @RequestHeader("accessToken") String accessToken) throws Exception {
        ResponseOrderDetailsDto responseOrderDetailsDto = mypageService.getOrderDetails(RequestOrderDetailsDto.builder().
                email(jwtUtils.getEmailFromToken(accessToken)).historiesId(historiesId).build());
        return ResponseEntity.status(HttpStatus.OK).body(responseOrderDetailsDto);
    }


    @GetMapping("/mp/sell")
    public ResponseEntity getSellInfo(@RequestHeader("accessToken") String accessToken) {
        String email = jwtUtils.getEmailFromToken(accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(mypageService.getSellHistoryList(email));
    }
}
=======
//
//    @GetMapping("/mp/buy/{historiesId}")
//    public ResponseEntity getOrderDetail(@PathVariable("historiesId") Long historiesId,
//                                         @RequestHeader("accessToken") String accessToken) throws Exception {
//        ResponseOrderDetailsDto responseOrderDetailsDto = mypageService.getOrderDetails(RequestOrderDetailsDto.builder().
//                email(jwtUtils.getEmailFromToken(accessToken)).historiesId(historiesId).build());
//        return ResponseEntity.status(HttpStatus.OK).body(responseOrderDetailsDto);
//    }
//
//    @GetMapping("/mp/sell")
//    public ResponseEntity getSellInfo(@RequestHeader("accessToken") String accessToken) {
//        String email = jwtUtils.getEmailFromToken(accessToken);
//        return ResponseEntity.status(HttpStatus.OK).body(mypageService.getSellHistoryList(email));
//    }
//}
>>>>>>> fa2c890fac06c4700776050349df8dda36a0037a
