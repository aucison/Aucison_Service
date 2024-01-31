package com.example.aucison_service.service.hidden;

import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.member.entity.MembersEntity;
import com.example.aucison_service.jpa.member.entity.MembersInfoEntity;
import com.example.aucison_service.jpa.member.repository.MembersInfoRepository;
import com.example.aucison_service.jpa.member.repository.MembersRepository;
import com.example.aucison_service.service.member.MemberDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreditServiceImpl implements CreditService {
    private final MembersRepository membersRepository;
    private final MembersInfoRepository membersInfoRepository;

    public CreditServiceImpl(MembersRepository membersRepository, MembersInfoRepository membersInfoRepository) {
        this.membersRepository = membersRepository;
        this.membersInfoRepository = membersInfoRepository;
    }

    @Override
    public float updateCredit(MemberDetails principal, float creditChange) {
        String email = principal.getMember().getEmail();

        MembersEntity member = membersRepository.findByEmail(email);
        if (member == null) {
            throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
        }

        MembersInfoEntity membersInfo = membersInfoRepository.findByMembersEntity(member);
        if (membersInfo == null) {
            throw new AppException(ErrorCode.MEMBERS_INFO_NOT_FOUND);
        }

        float currentCredit = membersInfo.getCredit();
        float updatedCredit = currentCredit + creditChange;

        membersInfo.updateCredit(updatedCredit);

        return updatedCredit;
    }
}
