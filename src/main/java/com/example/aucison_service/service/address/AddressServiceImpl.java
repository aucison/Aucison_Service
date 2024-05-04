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
import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
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
        validateRequestAddressDto(requestAddressDto);

        String email = principal.getMember().getEmail();

        MembersEntity member = membersRepository.findByEmail(email);
        if (member == null) {
            throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
        }

        MembersInfoEntity membersInfo = membersInfoRepository.findByMembersEntity(member);
        if (membersInfo == null) {
            throw new AppException(ErrorCode.MEMBERS_INFO_NOT_FOUND);
        }

        //TODO: 사용자 추가정보 등록 시 최초 배송지를 받기 때문에 테스트 후 최초 배송지 검증 로직 삭제
//        Boolean isPrimary = null;
//        //최초 배송지 등록인지 검사
//        if (addressesRepository.findAllByMembersInfoEntity(membersInfo).isEmpty()) {
//            isPrimary = true;
//        } else {
//            isPrimary = requestAddressDto.isPrimary();
//
//            // 동일한 배송지명이 있는지 검사
//            if (addressesRepository.existsByAddrNameAndMembersInfoEntity(requestAddressDto.getAddrName(), membersInfo)) {
//                throw new AppException(ErrorCode.ADDRESS_NAME_ALREADY_EXISTS); // 배송지명이 이미 존재하면 예외 발생
//            }
//
//            //대표 배송지 있는지 검사
//            if (requestAddressDto.isPrimary()) {
//                // 모든 기존 주소에서 대표 배송지 설정 제거
//                List<AddressesEntity> allAddresses = addressesRepository.findAllByMembersInfoEntity(membersInfo);
//                for (AddressesEntity addr : allAddresses) {
//                    if (addr.isPrimary()) {
//                        addr.updateIsPrimary(false);
//                        addressesRepository.save(addr);
//                    }
//                }
//            }
//
//        }

        Boolean isPrimary = requestAddressDto.isPrimary();

        // 동일한 배송지명이 있는지 검사
        if (addressesRepository.existsByAddrNameAndMembersInfoEntity(requestAddressDto.getAddrName(), membersInfo)) {
            throw new AppException(ErrorCode.ADDRESS_NAME_ALREADY_EXISTS); // 배송지명이 이미 존재하면 예외 발생
        }

        //대표 배송지 있는지 검사
        if (requestAddressDto.isPrimary()) {
            // 모든 기존 주소에서 대표 배송지 설정 제거
            List<AddressesEntity> allAddresses = addressesRepository.findAllByMembersInfoEntity(membersInfo);
            for (AddressesEntity addr : allAddresses) {
                if (addr.isPrimary()) {
                    addr.updateIsPrimary(false);
                    addressesRepository.save(addr);
                }
            }
        }

        AddressesEntity address = AddressesEntity.builder()
                .addrName(requestAddressDto.getAddrName())
                .isPrimary(isPrimary)
                .zipNum(requestAddressDto.getZipNum())
                .addr(requestAddressDto.getAddr())
                .addrDetail(requestAddressDto.getAddrDetail())
                .name(requestAddressDto.getName())
                .tel(requestAddressDto.getTel())
                .membersInfoEntity(membersInfo)
                .build();

        addressesRepository.save(address);
    }

    private void validateRequestAddressDto(RequestAddressDto dto) {
        //isPrimary는 타입이 boolean이라 null이 될 수 없으므로 검증 X
        //외부 API 응답에 사용할 것이므로 AppException 사용

        if (StringUtils.isBlank(dto.getAddrName())) {
            throw new AppException(ErrorCode.ADDRESS_INVALID_INOUT);
        }
        if (StringUtils.isBlank(dto.getName())) {
            throw new AppException(ErrorCode.ADDRESS_INVALID_INOUT);
        }
        if (StringUtils.isBlank(dto.getTel())) {
            throw new AppException(ErrorCode.ADDRESS_INVALID_INOUT);
        }
        if (StringUtils.isBlank(dto.getAddr())) {
            throw new AppException(ErrorCode.ADDRESS_INVALID_INOUT);
        }
        if (StringUtils.isBlank(dto.getZipNum())) {
            throw new AppException(ErrorCode.ADDRESS_INVALID_INOUT);
        }
        if (StringUtils.isBlank(dto.getAddrDetail())) {
            throw new AppException(ErrorCode.ADDRESS_INVALID_INOUT);
        }
    }

    @Override
    @Transactional
    public void deleteAddress(MemberDetails principal, String addrName) {
        String email = principal.getMember().getEmail();

        MembersEntity member = membersRepository.findByEmail(email);
        if (member == null) {
            throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
        }

        MembersInfoEntity membersInfo = membersInfoRepository.findByMembersEntity(member);
        if (membersInfo == null) {
            throw new AppException(ErrorCode.MEMBERS_INFO_NOT_FOUND);
        }

        AddressesEntity address = addressesRepository.findByMembersInfoEntityAndAddrName(membersInfo, addrName);
        if (address == null) {
            throw new AppException(ErrorCode.ADDRESS_NOT_FOUND);
        } else if (address.isPrimary()) {
            throw new AppException((ErrorCode.PRIMARY_ADDRESS_CANNOT_BE_DELETED));
        }

        addressesRepository.delete(address);
    }

    //배송지 수정
    @Override
    @Transactional
    public void updateAddressByEmailAndAddrName(MemberDetails principal, String addrName,
                                                RequestUpdateAddressDto requestUpdateAddressDto) {
        String email = principal.getMember().getEmail();

        MembersEntity member = membersRepository.findByEmail(email);
        if (member == null) {
            throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
        }

        MembersInfoEntity membersInfo = membersInfoRepository.findByMembersEntity(member);
        if (membersInfo == null) {
            throw new AppException(ErrorCode.MEMBERS_INFO_NOT_FOUND);
        }

        // 대표 배송지를 새로 설정하는 경우
        if (requestUpdateAddressDto.isPrimary()) {
            // 모든 기존 주소에서 대표 배송지 설정 제거
            List<AddressesEntity> allAddresses = addressesRepository.findAllByMembersInfoEntity(membersInfo);
            for (AddressesEntity addr : allAddresses) {
                if (addr.isPrimary()) {
                    addr.updateIsPrimary(false);
                    addressesRepository.save(addr);
                }
            }
        }

        AddressesEntity address = addressesRepository.findByMembersInfoEntityAndAddrName(membersInfo, addrName);
        if (address == null) {
            throw new AppException(ErrorCode.ADDRESS_NOT_FOUND);
        }

        // 엔티티의 update 메소드를 호출하여 주소 정보 업데이트
        address.update(requestUpdateAddressDto);

        addressesRepository.save(address);
    }
    @Override
    @Transactional(readOnly = true)
    public List<ResponseAddressDto> getAddressInfo(MemberDetails principal) {
        String email = principal.getMember().getEmail();

        MembersEntity member = membersRepository.findByEmail(email);
        if (member == null) {
            throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
        }

        MembersInfoEntity membersInfo = membersInfoRepository.findByMembersEntity(member);
        if (membersInfo == null) {
            throw new AppException(ErrorCode.MEMBERS_INFO_NOT_FOUND);
        }

        List<AddressesEntity> addresses = addressesRepository.findAllByMembersInfoEntity(membersInfo);
        if (addresses.isEmpty()) {
            throw new AppException(ErrorCode.ADDRESSES_NOT_FOUND);
        }

        return addresses.stream()
                .map(address -> ResponseAddressDto.builder()
                        .isPrimary(address.isPrimary())
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

        MembersEntity member = membersRepository.findByEmail(email);
        if (member == null) {
            throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
        }

        MembersInfoEntity membersInfo = membersInfoRepository.findByMembersEntity(member);
        if (membersInfo == null) {
            throw new AppException(ErrorCode.MEMBERS_INFO_NOT_FOUND);
        }

        AddressesEntity address = addressesRepository.findByMembersInfoEntityAndAddrName(membersInfo, addrName);
        if (address == null) {
            throw new AppException(ErrorCode.ADDRESS_NOT_FOUND);
        }

        return AddrInfoResponseDto.builder()
                .addrName(address.getAddrName())
                .name(address.getName())
                .tel(address.getTel())
                .zipCode(address.getZipNum())
                .addr(address.getAddr())
                .addrDetail(address.getAddrDetail())
                .build();
    }
}
