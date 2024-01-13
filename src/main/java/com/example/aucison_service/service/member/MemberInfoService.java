package com.example.aucison_service.service.member;

import com.example.aucison_service.dto.auth.MemberAdditionalInfoRequestDto;
import org.springframework.stereotype.Service;


public interface MemberInfoService {
    //사용자 추가정보 저장
    void saveMemberAdditionalInfo(MemberDetails principal, MemberAdditionalInfoRequestDto requestDto);
}
