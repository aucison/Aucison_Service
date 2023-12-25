package com.example.aucison_service.service.member;



import com.example.aucison_service.dto.wish.WishRequestDto;
import com.example.aucison_service.dto.wish.WishResponseDto;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.member.MembersEntity;
import com.example.aucison_service.jpa.member.MembersRepository;
import com.example.aucison_service.jpa.member.WishesEntity;
import com.example.aucison_service.jpa.member.WishesRepository;
import com.example.aucison_service.jpa.product.ProductsEntity;
import com.example.aucison_service.jpa.product.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishServiceImpl implements WishService {

    private final MembersRepository membersRepository;
    private final WishesRepository wishesRepository;
    private final ProductsRepository productsRepository;


    @Autowired
    public WishServiceImpl(MembersRepository membersRepository, WishesRepository wishesRepository, ProductsRepository productsRepository){
        this.membersRepository=membersRepository;
        this.wishesRepository=wishesRepository;
        this.productsRepository=productsRepository;
    }

    private void validatePrincipal(MemberDetails principal) {
        if (principal == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    //찜 추가
    @Override
    public void addWish(WishRequestDto wishRequestDto, MemberDetails principal) {
        validatePrincipal(principal);

//        WishesEntity wish = WishesEntity.builder();



    }

    //찜 삭제
    @Override
    public void deleteWish(WishRequestDto wishRequestDto, MemberDetails principal) {

    }

    //찜 목록 조회
    @Override
    public List<WishResponseDto> getMemberWishList(MemberDetails principal) {

        MembersEntity member = membersRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        List<WishesEntity> wishes = wishesRepository.findByMembersEntity(member);

        return wishes.stream()
                .map(wish -> {
                    ProductsEntity product = productsRepository.findByProductsId()
                }
    }
}
