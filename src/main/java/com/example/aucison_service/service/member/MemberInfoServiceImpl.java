package com.example.aucison_service.service.member;

import com.example.aucison_service.dto.auth.MemberAdditionalInfoRequestDto;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.member.entity.AddressesEntity;
import com.example.aucison_service.jpa.member.entity.MembersEntity;
import com.example.aucison_service.jpa.member.entity.MembersInfoEntity;
import com.example.aucison_service.jpa.member.repository.AddressesRepository;
import com.example.aucison_service.jpa.member.repository.MembersInfoRepository;
import com.example.aucison_service.jpa.member.repository.MembersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberInfoServiceImpl implements MemberInfoService{
    private final MembersRepository membersRepository;
    private final MembersInfoRepository membersInfoRepository;
    private final AddressesRepository addressesRepository;

    @Autowired
    public MemberInfoServiceImpl(MembersRepository membersRepository, MembersInfoRepository membersInfoRepository,
                                 AddressesRepository addressesRepository) {
        this.membersRepository = membersRepository;
        this.membersInfoRepository = membersInfoRepository;
        this.addressesRepository = addressesRepository;
    }
    @Override
    @Transactional
    public void saveMemberAdditionalInfo(MemberDetails principal, MemberAdditionalInfoRequestDto requestDto) {
        String email = principal.getMember().getEmail();

        MembersEntity member = membersRepository.findByEmail(email);
        if (member == null) {
            throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 회원의 닉네임 업데이트
        member.updateNickname(requestDto.getNickName());

        // 회원 추가 정보 엔티티 생성
        MembersInfoEntity membersInfo = MembersInfoEntity.builder()
                .phone(requestDto.getPhone())
                .subEmail(requestDto.getSubEmail())
                .credit(100000000.0f) // 기본 credit을 1억으로 초기화
                .membersEntity(member)
                .build();

        membersInfoRepository.save(membersInfo);

        AddressesEntity address = AddressesEntity.builder()
                .addrName(requestDto.getAddrName())
                .isPrimary(true)  // 최초 주소는 항상 대표 주소로 설정
                .zipNum(requestDto.getZipNum())
                .addr(requestDto.getAddr())
                .addrDetail(requestDto.getAddrDetail())
                .name(requestDto.getName())
                .tel(requestDto.getTel())
                .membersInfoEntity(membersInfo)
                .build();

        addressesRepository.save(address);
    }
}
