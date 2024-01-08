package com.example.aucison_service.service.address;

import com.example.aucison_service.dto.mypage.RequestAddressDto;
import com.example.aucison_service.dto.mypage.RequestUpdateAddressDto;
import com.example.aucison_service.dto.mypage.ResponseAddressDto;
import com.example.aucison_service.dto.payments.AddrInfoResponseDto;
import com.example.aucison_service.service.member.MemberDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AddressService {
    // 배송지 등록
    void addAddress(MemberDetails principal, RequestAddressDto requestAddressDto);
    void deleteAddress(MemberDetails principal, String addrName);
    void updateAddressByEmailAndAddrName(MemberDetails principal, String addrName, RequestUpdateAddressDto requestUpdateAddressDto);
    List<ResponseAddressDto> getAddressInfo(MemberDetails principal);
    AddrInfoResponseDto getShippingInfo(Long productsId, MemberDetails principal, String addrName);
}
