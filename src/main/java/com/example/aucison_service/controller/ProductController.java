package com.example.aucison_service.controller;



import com.example.aucison_service.dto.ApiResponse;
import com.example.aucison_service.dto.aucs_sale.AucsProductResponseDto;
import com.example.aucison_service.dto.aucs_sale.SaleProductResponseDto;
import com.example.aucison_service.dto.product.ProductDetailResponseDto;
import com.example.aucison_service.dto.product.ProductRegisterRequestDto;
import com.example.aucison_service.dto.search.ProductSearchRequestDto;
import com.example.aucison_service.dto.search.ProductSearchResponseDto;
import com.example.aucison_service.service.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
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

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService){
        this.productService = productService;
    }



    //모든 경매(AUCS) + 핸드메이드(HAND) 상품 반환
    @GetMapping("/aucs/hand/list")
    public ResponseEntity<List<AucsProductResponseDto>> getAllAucsHandProducts() {
        List<AucsProductResponseDto> products = productService.getAllAucsHandProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }


    //모든 경매(AUCS) + 일반(NORM) 상품 반환
    @GetMapping("/aucs/norm/list")
    public ResponseEntity<List<AucsProductResponseDto>> getAllAucsNormProducts() {
        List<AucsProductResponseDto> products = productService.getAllAucsNormProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    //모든 비경매(SALE) + 핸드메이드(HAND) 상품 반환
    @GetMapping("/sale/hand/list")
    public ResponseEntity<List<SaleProductResponseDto>> getAllSaleHandProducts() {
        List<SaleProductResponseDto> products = productService.getAllSaleHandProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    //모든 비경매(SALE) + 일반(NORM) 상품 반환
    @GetMapping("/sale/norm/list")
    public ResponseEntity<List<SaleProductResponseDto>> getAllSaleNormProducts() {
        List<SaleProductResponseDto> products = productService.getAllSaleNormProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    //상품등록
    @PostMapping("/product/register")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> registerProduct(@RequestBody ProductRegisterRequestDto dto,
                                             @AuthenticationPrincipal OAuth2User principal) {
        productService.registerProduct(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    //상품 검색
    @GetMapping("/search")
    public ResponseEntity<ProductSearchResponseDto> searchProductByName(@RequestParam String name) {//쿼리 파라미터 방식(Query Parameter) -> 테스트 ?name=th
        try {
            // 서비스 계층을 통한 상품 검색 로직 수행
            ProductSearchResponseDto response = productService.searchProductByName(name);

            if (response == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // 상품 상세 정보 조회
    @GetMapping("/detail/{productId}")
    public ResponseEntity<ProductDetailResponseDto> getProductDetail(@PathVariable Long productId) {    //경로변수 방식(Path Variable) -> 테스트 /123
        try {
            ProductDetailResponseDto productDetail = productService.getProductDetail(productId);

            if (productDetail == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return new ResponseEntity<>(productDetail, HttpStatus.OK);
        } catch (Exception e) {
            // 예외 처리, 예를 들어 로깅이나 에러 메시지 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
