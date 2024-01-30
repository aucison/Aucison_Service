package com.example.aucison_service.service.member;



import com.example.aucison_service.dto.board.PostCRUDResponseDto;
import com.example.aucison_service.dto.wish.WishRequestDto;
import com.example.aucison_service.dto.wish.WishResponseDto;
import com.example.aucison_service.dto.wish.WishSimpleResponseDto;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.member.entity.MembersEntity;
import com.example.aucison_service.jpa.member.repository.MembersRepository;
import com.example.aucison_service.jpa.member.entity.WishesEntity;
import com.example.aucison_service.jpa.member.repository.WishesRepository;

import com.example.aucison_service.jpa.product.entity.AucsInfosEntity;
import com.example.aucison_service.jpa.product.entity.ProductsEntity;
import com.example.aucison_service.jpa.product.repository.ProductsRepository;
import com.example.aucison_service.jpa.product.entity.SaleInfosEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public WishSimpleResponseDto addWish(WishRequestDto wishRequestDto, MemberDetails principal) {
        validatePrincipal(principal);


        //사용자 정보 가져옴
        MembersEntity member = membersRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        // 상품 정보 조회
        Long productId = wishRequestDto.getProductsId();
        ProductsEntity product = productsRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

         //중복 찜 확인
        if(wishesRepository.existsByMembersEntityAndProductId(member, productId)) {
            throw new AppException(ErrorCode.DUPLICATE_WISH);
        }

        // 찜 객체 생성 및 저장
        WishesEntity wish = WishesEntity.builder()
                .membersEntity(member)
                .productId(product.getProductsId())
                .build();
        wishesRepository.save(wish);

        return WishSimpleResponseDto.builder().productId(wishRequestDto.getProductsId()).build();

    }

    //찜 삭제
    @Override
    @Transactional
    public WishSimpleResponseDto deleteWish(WishRequestDto wishRequestDto, MemberDetails principal) {



        //사용자 정보 가져옴
        MembersEntity member = membersRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        // 찜 정보 조회
        Long productId = wishRequestDto.getProductsId();
        WishesEntity wish = wishesRepository.findByMembersEntityAndProductId(member, productId)
                .orElseThrow(() -> new AppException(ErrorCode.WISH_NOT_FOUND));


        // 찜 삭제
        wishesRepository.delete(wish);
        return WishSimpleResponseDto.builder().productId(wishRequestDto.getProductsId()).build();
    }

    //찜 목록 조회
    @Override
    public List<WishResponseDto> getMemberWishList(MemberDetails principal) {

        MembersEntity member = membersRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        List<WishesEntity> wishes = wishesRepository.findByMembersEntity(member);

        return wishes.stream()

                .map(wish -> {  //wish이용하여 하나하나 탐색
                    ProductsEntity product = productsRepository.findById(wish.getProductId())
                            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

                    WishResponseDto.WishResponseDtoBuilder builder = WishResponseDto.builder()
                            .wishesId(wish.getWishesId())
                            .name(product.getName())
                            .category(product.getCategory())
                            .kind(product.getKind())
                            .tags(product.getTags());

                    if ("AUCS".equals(product.getCategory())) {
                        // 경매 상품인 경우
                        AucsInfosEntity aucInfo = product.getAucsInfosEntity();

                        builder.price(aucInfo.getStartPrice())
                                .end(aucInfo.getEnd());
                    } else if ("SALE".equals(product.getCategory())) {
                        // 비경매 상품인 경우
                        SaleInfosEntity saleInfo = product.getSaleInfosEntity();

                        builder.nowPrice(saleInfo.getPrice());
                    }
                    return builder.build();
                })
                .collect(Collectors.toList());

    }
}
