package com.example.aucison_service.service.product;

import com.example.Aucsion_Product_Service.dto.auc_nor.AucsProductResponseDto;
import com.example.Aucsion_Product_Service.dto.auc_nor.SaleProductResponseDto;
import com.example.Aucsion_Product_Service.dto.product.ProductDetailResponseDto;
import com.example.Aucsion_Product_Service.dto.product.ProductRegisterRequestDto;
import com.example.Aucsion_Product_Service.dto.search.ProductSearchResponseDto;

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
