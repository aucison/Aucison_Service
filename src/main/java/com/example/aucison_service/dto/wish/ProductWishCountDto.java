package com.example.aucison_service.dto.wish;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductWishCountDto {

    private Long productId;
    private Long wishCount;
}
