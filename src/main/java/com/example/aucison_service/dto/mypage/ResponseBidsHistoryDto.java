package com.example.aucison_service.dto.mypage;


import ch.qos.logback.core.status.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ResponseBidsHistoryDto {
    private Status status;
    private Date bidsAt; // 응찰 시간(수정이 필요합니다)
}
