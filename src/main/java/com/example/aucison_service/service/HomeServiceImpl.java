package com.example.aucison_service.service;


import com.example.aucison_service.dto.home.HomeResponseDto;
import com.example.aucison_service.dto.home.ProductMainResponseDto;
import com.example.aucison_service.jpa.product.entity.BidCountsEntity;
import com.example.aucison_service.jpa.product.entity.ProductsEntity;
import com.example.aucison_service.jpa.product.repository.BidCountsRepository;
import com.example.aucison_service.jpa.product.repository.ProductsRepository;
import com.example.aucison_service.service.member.MemberDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class HomeServiceImpl implements HomeService {

    private final ProductsRepository productsRepository;
    private final BidCountsRepository bidCountsRepository;

    @Autowired
    public HomeServiceImpl (ProductsRepository productsRepository, BidCountsRepository bidCountsRepository){
        this.productsRepository = productsRepository;
        this.bidCountsRepository = bidCountsRepository;
    }


    @Override
    public HomeResponseDto getHomeData(@AuthenticationPrincipal MemberDetails principal) {

        String userNickname = null;
        if (principal != null) {
            userNickname = ((MemberDetails) principal).getMember().getNickname();
        }

        // 실시간 인기 상품 목록 가져오기 -> 10개 가져오며 미달시 미달한 갯수만큼 가져옴
        List<ProductMainResponseDto> popularProducts = bidCountsRepository.findTop10ByOrderByTotCntDesc()
                .stream()
                .map(bidCount -> {
                    ProductsEntity product = productsRepository.findById(bidCount.getProductsId()).orElse(null);
                    return product != null ? createProductMainResponseDto(product) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());



        // 최신 등록 상품 목록 가져오기 -> 10개 가져오며 미달시 미달한 갯수만큼 가져옴
        List<ProductMainResponseDto> recentProducts = productsRepository.findTop10ByOrderByCreatedDateDesc()
                .stream()
                .map(product -> createProductMainResponseDto(product))
                .collect(Collectors.toList());


        return HomeResponseDto.builder()
                .userNickname(userNickname)
                .popularAucsProducts(popularProducts)
                .recentSaleProducts(recentProducts)
                .build();
    }

    private ProductMainResponseDto createProductMainResponseDto(ProductsEntity product) {
        String firstImageUrl = product.getImages().isEmpty() ? null : product.getImages().get(0).getUrl();
        Float price = null;
        Float highestPrice = null;
        LocalDateTime end = null;

        if ("SALE".equals(product.getCategory()) && product.getSaleInfosEntity() != null) {
            price = product.getSaleInfosEntity().getPrice();
        }
        if ("AUCS".equals(product.getCategory()) && product.getAucsInfosEntity() != null) {
            highestPrice = product.getAucsInfosEntity().getHighestPrice();
            end = product.getAucsInfosEntity().getEnd();
        }

        // totCnt 값은 BidCountsEntity에서 가져옴
        BidCountsEntity bidCount = bidCountsRepository.findByProductsId(product.getProductsId());

        return ProductMainResponseDto.builder()
                .productId(product.getProductsId())
                .name(product.getName())
                .kind(product.getKind())
                .category(product.getCategory())
                .imgUrl(firstImageUrl)
                .totCnt(bidCount.getTotCnt())
                .end(end)
                .high(highestPrice)
                .price(price)
                .build();
    }

}
