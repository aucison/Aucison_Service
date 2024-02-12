package com.example.aucison_service.dto.home;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HomeResponseDto {

    private String userNickname;
    private List<ProductMainResponseDto> popularAucsProducts;   //인기
    private List<ProductMainResponseDto> recentSaleProducts;    //최근



}
