package com.example.aucison_service.controller;


import com.example.Aucsion_Product_Service.dto.ApiResponse;
import com.example.Aucsion_Product_Service.dto.auc_nor.AucsProductResponseDto;
import com.example.Aucsion_Product_Service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product-service")
public class ProductController {


    //공부용 주석
    //@Autowired란?
    //@Autowired가 붙은 필드, 생성자, 또는 메소드에 대해서 스프링은 해당 타입의 빈(Bean)을 찾아 자동으로 주입

    //아래에서 보면 생성자를 통한 의존성 주입을 하고있음
    //왜 이렇게 할까?
    //유연성 - 의존성을 직접 생성하지 않고 외부에서 주입받기 때문에 코드 변경 없이 다른 구현을 사용할 수 있음
    //테스트 용이
    //결합도 감소 - 클래스간의 결합도가 낮아져서 유지보수가 쉬움
    //자동 설정과 설정의 중앙화 : 예를 들어 Environment 객체를 통해 애플리케이션의 환경 설정에 쉽게 접근


    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService){
        this.productService = productService;
    }


/*
    @GetMapping("/auc/nothand/list")
    public ResponseEntity<List<AucsProductResponseDto>> getAllAucNothandProducts() {
        List<AucsProductResponseDto> products = productService.getAllAucNothandProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/auc/hand/list")
    public ResponseEntity<List<AucsProductResponseDto>> getAllAucHandProducts() {
        List<AucsProductResponseDto> products = productService.getAllAucHandProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/nor/nothand/list")
    public ResponseEntity<List<SaleProductResponseDto>> getAllNorNothandProducts() {
        List<SaleProductResponseDto> products = productService.getAllNorNothandProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/nor/hand/list")
    public ResponseEntity<List<SaleProductResponseDto>> getAllNorHandProducts() {
        List<SaleProductResponseDto> products = productService.getAllNorHandProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
    */

    //ApiResponse형태로 변경하기

    //apiresponse를 사용할 경우 일관성이 높아지고 가독성이 높아진다
    //다만 reponseentity를 사용할 경우 좀 더 세부적인 컨트롤이 가능하다

    @GetMapping("/auc/nothand/list")
    public ApiResponse<List<AucsProductResponseDto>> getAllAucNothandProducts() {
        List<AucsProductResponseDto> products = productService.getAllAucsNormProducts();
        return ApiResponse.createSuccess(products);
    }

}
