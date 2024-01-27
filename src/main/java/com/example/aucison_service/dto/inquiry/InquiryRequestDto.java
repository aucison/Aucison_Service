package com.example.aucison_service.dto.inquiry;


import com.example.aucison_service.enums.QStatusEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InquiryRequestDto {

    private String email;
    private String post;
    private String comment;
    private QStatusEnum qStatus;
}
