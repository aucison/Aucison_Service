package com.example.aucison_service.service.hidden;

import com.example.aucison_service.service.member.MemberDetails;

public interface CreditService {
    float updateCredit(MemberDetails principal, float creditChange);
}
