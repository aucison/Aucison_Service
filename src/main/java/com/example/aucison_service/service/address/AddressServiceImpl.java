package com.example.aucison_service.service.address;

import com.example.aucison_service.dto.mypage.RequestAddressDto;
import com.example.aucison_service.dto.mypage.RequestUpdateAddressDto;
import com.example.aucison_service.dto.mypage.ResponseAddressDto;
import com.example.aucison_service.dto.payments.AddrInfoResponseDto;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.member.entity.AddressesEntity;
import com.example.aucison_service.jpa.member.entity.MembersEntity;
import com.example.aucison_service.jpa.member.entity.MembersInfoEntity;
import com.example.aucison_service.jpa.member.repository.AddressesRepository;
import com.example.aucison_service.jpa.member.repository.MembersInfoRepository;
import com.example.aucison_service.jpa.member.repository.MembersRepository;
import com.example.aucison_service.service.member.MemberDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AddressServiceImpl implements AddressService {
    private final MembersRepository membersRepository;
    private final MembersInfoRepository membersInfoRepository;
    private final AddressesRepository addressesRepository;

    @Autowired
    public AddressServiceImpl(MembersRepository membersRepository, MembersInfoRepository membersInfoRepository,
                              AddressesRepository addressesRepository) {
        this.membersRepository = membersRepository;
        this.membersInfoRepository = membersInfoRepository;
        this.addressesRepository = addressesRepository;
    }

    @Override
    @Transactional
    public void addAddress(MemberDetails principal, RequestAddressDto requestAddressDto) {
        String email = principal.getMember().getEmail();
        MembersEntity member = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND)); // 사용자 조회, 없으면 예외 발생

        MembersInfoEntity membersInfo = Optional.ofNullable(membersInfoRepository.findByMembersEntity(member))
                .orElseThrow(() -> new AppException(ErrorCode.HISTORY_NOT_FOUND)); // 사용자 상세정보 조회, 없으면 예외 발생

        // 동일한 배송지명이 있는지 검사
        if (addressesRepository.existsByAddrNameAndMembersInfoEntity(requestAddressDto.getAddrName(), membersInfo)) {
            throw new AppException(ErrorCode.ADDRESS_NAME_ALREADY_EXISTS); // 배송지명이 이미 존재하면 예외 발생
        }

        AddressesEntity address = AddressesEntity.builder()
                .addrName(requestAddressDto.getAddrName())
                .zipNum(requestAddressDto.getZipNum())
                .addr(requestAddressDto.getAddr())
                .addrDetail(requestAddressDto.getAddrDetail())
                .name(requestAddressDto.getName())
                .tel(requestAddressDto.getTel())
                .membersInfoEntity(membersInfo)
                .build();

        addressesRepository.save(address);
    }

    @Override
    @Transactional
    public void deleteAddress(MemberDetails principal, String addrName) {
        String email = principal.getMember().getEmail();
        MembersEntity member = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND)); // 사용자 조회, 없으면 예외 발생

        MembersInfoEntity membersInfo = member.getMembersInfoEntity();

        AddressesEntity address = addressesRepository.findByMembersInfoEntityAndAddrName(membersInfo, addrName);

        addressesRepository.delete(address);
    }

    //배송지 수정
    @Override
    @Transactional
    public void updateAddressByEmailAndAddrName(MemberDetails principal, String addrName,
                                                RequestUpdateAddressDto requestUpdateAddressDto) {
        String email = principal.getMember().getEmail();
        MembersEntity member = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        MembersInfoEntity membersInfo = member.getMembersInfoEntity();

        AddressesEntity address = addressesRepository.findByMembersInfoEntityAndAddrName(membersInfo, addrName);

        // 엔티티의 update 메소드를 호출하여 주소 정보 업데이트
        address.update(requestUpdateAddressDto);

        addressesRepository.save(address);
    }
    @Override
    @Transactional(readOnly = true)
    public List<ResponseAddressDto> getAddressInfo(MemberDetails principal) {
        String email = principal.getMember().getEmail();
        MembersEntity member = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND)); // 사용자 조회, 없으면 예외 발생

        MembersInfoEntity membersInfo = member.getMembersInfoEntity();
        List<AddressesEntity> addresses = addressesRepository.findAllByMembersInfoEntity(membersInfo);

        return addresses.stream()
                .map(address -> ResponseAddressDto.builder()
                        .addrName(address.getAddrName())
                        .name(address.getName())
                        .zipNum(address.getZipNum())
                        .addr(address.getAddr())
                        .addrDetail(address.getAddrDetail())
                        .tel(address.getTel())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AddrInfoResponseDto getAddressInfoByAddrName(MemberDetails principal, String addrName) {  //배송지명으로 배송지 조회
        String email = principal.getMember().getEmail();

        MembersEntity membersEntity = membersRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        MembersInfoEntity membersInfoEntity = membersInfoRepository.findByMembersEntity(membersEntity);

        AddressesEntity addressesEntity = addressesRepository.findByMembersInfoEntityAndAddrName(membersInfoEntity, addrName);

        if (addressesEntity == null) {
            throw new AppException(ErrorCode.SHIPPING_INFO_NOT_FOUND);
        }
        return AddrInfoResponseDto.builder()
                .addrName(addressesEntity.getAddrName())
                .name(addressesEntity.getName())
                .tel(addressesEntity.getTel())
                .zipCode(addressesEntity.getZipNum())
                .addr(addressesEntity.getAddr())
                .addrDetail(addressesEntity.getAddrDetail())
                .build();
    }
}
