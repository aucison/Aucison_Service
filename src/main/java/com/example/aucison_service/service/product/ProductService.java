package com.example.aucison_service.service.product;


import com.example.aucison_service.dto.aucs_sale.AucsProductResponseDto;
import com.example.aucison_service.dto.aucs_sale.SaleProductResponseDto;
import com.example.aucison_service.dto.product.ProductDetailResponseDto;
import com.example.aucison_service.dto.product.ProductRegisterRequestDto;
import com.example.aucison_service.dto.search.ProductSearchResponseDto;
import com.example.aucison_service.dto.wish.ProductWishCountDto;
import com.example.aucison_service.service.member.MemberDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;

public interface ProductService {

    List<AucsProductResponseDto> getAllAucsHandProducts();
    List<AucsProductResponseDto> getAllAucsNormProducts();
    List<SaleProductResponseDto> getAllSaleHandProducts();
    List<SaleProductResponseDto> getAllSaleNormProducts();

    void registerProduct(ProductRegisterRequestDto dto, @AuthenticationPrincipal MemberDetails principal);

    List<ProductSearchResponseDto> searchProductByName(String name);

    //상품 상세 정보 조회하기
    ProductDetailResponseDto getProductDetail(Long productsId);

    //상품의 찜 갯수
//    List<ProductWishCountDto> getWishCounts();


}
