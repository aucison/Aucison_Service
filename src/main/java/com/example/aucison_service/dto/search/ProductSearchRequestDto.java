package com.example.aucison_service.dto.search;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductSearchRequestDto {
    //상품 검색시 사용하는 Dto


    private String name;
}
