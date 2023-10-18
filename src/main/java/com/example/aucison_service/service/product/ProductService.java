package com.example.aucison_service.service.product;


import com.example.aucison_service.dto.aucs_sale.AucsProductResponseDto;
import com.example.aucison_service.dto.aucs_sale.SaleProductResponseDto;
import com.example.aucison_service.dto.product.ProductDetailResponseDto;
import com.example.aucison_service.dto.product.ProductRegisterRequestDto;
import com.example.aucison_service.dto.search.ProductSearchResponseDto;

import java.util.List;

public interface ProductService {

    List<AucsProductResponseDto> getAllAucsHandProducts();
    List<AucsProductResponseDto> getAllAucsNormProducts();
    List<SaleProductResponseDto> getAllSaleHandProducts();
    List<SaleProductResponseDto> getAllSaleNormProducts();

    void registerProduct(ProductRegisterRequestDto dto);

    ProductSearchResponseDto searchProductByName(String name, String email);

    //상품 상세 정보 조회하기
    ProductDetailResponseDto getProductDetail(String product_code);



}
