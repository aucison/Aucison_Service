package com.example.aucison_service.service.product;

import com.example.aucison_service.jpa.product.ProductsRepository;
import com.example.aucison_service.elastic.ProductsSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductsIndexService {
    private final ProductsRepository productsRepository;
    private final ProductsSearchRepository productsSearchRepository;

    @Autowired
    public ProductsIndexService(ProductsRepository productsRepository, ProductsSearchRepository productsSearchRepository){
        this.productsRepository = productsRepository;
        this.productsSearchRepository = productsSearchRepository;
    }

    // 데이터 색인화 메소드
    public void indexProducts() {
        productsRepository.findAll().forEach(productsSearchRepository::save);
    }
}
