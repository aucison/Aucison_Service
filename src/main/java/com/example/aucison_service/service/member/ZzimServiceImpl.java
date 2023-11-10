//package com.example.aucison_service.service.member;
//
//
//import com.example.aucison_service.dto.zzim.ResponseZzimHistoryDto;
//import com.example.aucison_service.jpa.member.MembersRepository;
//import com.example.aucison_service.jpa.member.WishesEntity;
//import com.example.aucison_service.jpa.member.WishesRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class ZzimServiceImpl implements ZzimService {
//    ;
//    private final MembersRepository membersRepository;
//    private final WishesRepository wishesRepository;
//
//    @Override
//    public List<ResponseZzimHistoryDto> getZzimHistoryList(String accessToken) {
//        String email = jwtUtils.getEmailFromToken(accessToken);
//        return wishesRepository.findByMembersEntity(membersRepository.findByEmail(email))
//                .stream()
//                .map(wishesEntity -> {
//                    return ResponseZzimHistoryDto.builder()
//                            .wishesId(wishesEntity.getId())
////                            .name()
////                            .summary()
////                            .imgUrl()
////                            .category()
////                            .price()
////                            .nowPrice()
//                            .build();
//                }).collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional
//    public void deleteZzim(Long wishesId) throws Exception {
//        Optional<WishesEntity> wishes = wishesRepository.findById(wishesId);
//        if(wishes == null) {
//            throw new RuntimeException("error");
//        }
//        wishesRepository.deleteById(wishesId);
//    }
//}
