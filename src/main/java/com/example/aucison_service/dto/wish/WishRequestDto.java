package com.example.aucison_service.dto.wish;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class WishRequestDto {
    private Long productsId;
}
