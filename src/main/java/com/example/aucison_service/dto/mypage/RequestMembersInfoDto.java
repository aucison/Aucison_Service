package com.example.aucison_service.dto.mypage;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RequestMembersInfoDto {
    private String nickName; //별명


    private String subEmail; // 서브 이메일
    private String phone; // 전화번호
    private MultipartFile imgUrl; // 프로필 사진 URL
}
