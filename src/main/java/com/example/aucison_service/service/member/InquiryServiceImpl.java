package com.example.aucison_service.service.member;

import com.example.aucison_service.dto.board.PostCRUDResponseDto;
import com.example.aucison_service.dto.inquiry.InquiryRequestDto;
import com.example.aucison_service.dto.inquiry.InquiryResponseDto;
import com.example.aucison_service.enums.PStatusEnum;
import com.example.aucison_service.enums.QStatusEnum;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.member.entity.InquirysEntity;
import com.example.aucison_service.jpa.member.repository.InquirysRepository;
import com.example.aucison_service.jpa.product.entity.PostsEntity;
import com.example.aucison_service.service.product.BoardServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InquiryServiceImpl implements InquiryService {
    private static final Logger logger = LoggerFactory.getLogger(InquiryServiceImpl.class);

    InquirysRepository inquirysRepository;

    @Autowired
    public InquiryServiceImpl(InquirysRepository inquirysRepository){
        this.inquirysRepository = inquirysRepository;
    }


    private void validatePrincipal(MemberDetails principal) {
        if (principal == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }


    //일반 문의
    @Override
    @Transactional
    public InquiryResponseDto registInquiryNorm(InquiryRequestDto dto, @AuthenticationPrincipal MemberDetails principal) {
        validatePrincipal(principal);

        //String email = principal.getAttribute("email");
        String email = principal.getMember().getEmail();

        InquirysEntity inquiry = InquirysEntity.builder()
                .qEmail(email)
                .qPost(dto.getPost())
                .qComment(dto.getComment())
                .qStatus(QStatusEnum.GREEN000)
                .build();

        InquirysEntity savedInquiry = inquirysRepository.save(inquiry);

        InquiryResponseDto responseDto = InquiryResponseDto.builder()
                .inquiryId(savedInquiry.getInauirysId())
                .build();

        return responseDto;
    }

    //신고 문의
    @Override
    @Transactional
    public InquiryResponseDto registDeclare(InquiryRequestDto dto, @AuthenticationPrincipal MemberDetails principal) {
        validatePrincipal(principal);

        //String email = principal.getAttribute("email");
        String email = principal.getMember().getEmail();

        InquirysEntity inquiry = InquirysEntity.builder()
                .qEmail(email)
                .qPost(dto.getPost())
                .qComment(dto.getComment())
                .qStatus(QStatusEnum.RED000)
                .build();

        InquirysEntity savedInquiry = inquirysRepository.save(inquiry);

        InquiryResponseDto responseDto = InquiryResponseDto.builder()
                .inquiryId(savedInquiry.getInauirysId())
                .build();

        return responseDto;
    }
}
