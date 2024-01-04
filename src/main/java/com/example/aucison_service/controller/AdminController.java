package com.example.aucison_service.controller;


import com.example.aucison_service.service.product.ProductsIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {


    private final ProductsIndexService productsIndexService;

    @Autowired
    public AdminController(ProductsIndexService productsIndexService) {
        this.productsIndexService = productsIndexService;
    }

    @PostMapping("/index/products")
    public ResponseEntity<String> indexProducts() {
        productsIndexService.indexProducts();
        return ResponseEntity.ok("Product indexing has been started.");
    }
}
