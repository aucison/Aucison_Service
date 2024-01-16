package com.example.aucison_service.dto.mypage;


import com.example.aucison_service.enums.OStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ResponseBidsHistoryDto {
    private OStatusEnum oStatus;
    private Date bidsAt; // 응찰 시간(수정이 필요합니다)
}
