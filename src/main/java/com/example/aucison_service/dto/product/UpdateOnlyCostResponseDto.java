package com.example.aucison_service.dto.product;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateOnlyCostResponseDto {
    private float nowPrice;
}
