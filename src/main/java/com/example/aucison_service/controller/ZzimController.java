
package com.example.aucison_service.controller;


import com.example.aucison_service.service.member.ZzimService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/member-service")
public class ZzimController {

    private final ZzimService zzimService;

    @GetMapping("/zzim")
    public ResponseEntity getWishList(@RequestHeader("accessToken") String accessToken) {
        return ResponseEntity.status(HttpStatus.OK).body(zzimService.getZzimHistoryList(accessToken));
    }

    @DeleteMapping("/zzim/{wishesId}")
    public ResponseEntity deleteWish(@PathVariable("wishesId") Long wishesId) throws Exception {
        zzimService.deleteZzim(wishesId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
