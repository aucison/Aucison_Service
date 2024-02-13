package com.example.aucison_service.service.product;


import com.example.aucison_service.dto.aucs_sale.AucsProductResponseDto;
import com.example.aucison_service.dto.aucs_sale.SaleProductResponseDto;
import com.example.aucison_service.dto.product.*;
import com.example.aucison_service.dto.search.ProductSearchResponseDto;
import com.example.aucison_service.service.member.MemberDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

public interface ProductService {

    List<AucsProductResponseDto> getAllAucsHandProducts();
    List<AucsProductResponseDto> getAllAucsNormProducts();
    List<SaleProductResponseDto> getAllSaleHandProducts();
    List<SaleProductResponseDto> getAllSaleNormProducts();

    void registerProduct(ProductRegisterRequestDto dto, @AuthenticationPrincipal MemberDetails principal);

    List<ProductSearchResponseDto> searchProductByName(String name);

    //상품 상세 정보 조회하기
    ProductDetailResponseDto getProductDetail(Long productsId, @AuthenticationPrincipal MemberDetails principal);

    //상품의 찜 갯수
//    List<ProductWishCountDto> getWishCounts();

    ProductRegisterFinshResponseDto finshReisterProduct(Long productId, @AuthenticationPrincipal MemberDetails principal);

    UpdateOnlyCostResponseDto updateOnlyCost(Long productId, @AuthenticationPrincipal MemberDetails principal);

    // 전체 상품 조회 with Pageable
    Page<ProductAllResponseDto> getAllProducts(Pageable pageable);

}
