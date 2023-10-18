package com.example.aucison_service.service.product;



import com.example.aucison_service.dto.aucs_sale.AucsProductResponseDto;
import com.example.aucison_service.dto.aucs_sale.SaleProductResponseDto;
import com.example.aucison_service.dto.product.ProductDetailResponseDto;
import com.example.aucison_service.dto.product.ProductRegisterRequestDto;
import com.example.aucison_service.dto.search.ProductSearchResponseDto;
import com.example.aucison_service.jpa.product.*;
import com.example.aucison_service.util.S3Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{


    ProductsRepository productsRepository;
    Sale_infosRepository sale_infosRepository;
    Aucs_infosRepository aucs_infosRepository;

    MemberServiceClient memberServiceClient;

    ShippingServiceClient shippingServiceClient;

    S3Utils s3Utils;


    @Autowired
    public ProductServiceImpl(ProductsRepository productsRepository, Sale_infosRepository sale_infosRepository, Aucs_infosRepository aucs_infosRepository, MemberServiceClient memberServiceClient, ShippingServiceClient shippingServiceClient, S3Utils s3Utils){
        this.productsRepository=productsRepository;
        this.aucs_infosRepository=aucs_infosRepository;
        this.sale_infosRepository=sale_infosRepository;
        this.memberServiceClient = memberServiceClient;
        this.shippingServiceClient = shippingServiceClient;
        this.s3Utils = s3Utils;
    }


    //해당 코드에 조건 추가 및 리펙토링이 필요하여 주석처리

//    @Override
//    public List<AucsProductResponseDto> getAllAucProducts() {
//        //모든 경매상품들 반환
//        List<ProductsEntity> products = productsRepository.findByCategory("AUC");
//
//        //가져온 리스트 [products]를 스트림형으로 변환 후 각각의 상품 정보를 AucProductResponseDto로 맵핑 -> 이를 반환
//        return products.stream().map(product ->
//                AucsProductResponseDto.builder()
//                        .name(product.getName())
//                        .created_at(product.getCreated_at())
//                        .information(product.getInformation())
//                        .summary(product.getSummary())
//                        .start_price(product.getAuc_infosEntity().getStart_price())
//                        .end(product.getAuc_infosEntity().getEnd())
//                        .bids_code(product.getAuc_infosEntity().getBids_code())
//                        .build()
//        ).collect(Collectors.toList());
//
//    }
//
//    @Override
//    public List<SaleProductResponseDto> getAllNorProducts() {
//        //모든 비경매상품(일반)들 반환
//
//        List<ProductsEntity> products = productsRepository.findByCategory("NOR");
//
//        return products.stream().map(product ->
//                SaleProductResponseDto.builder()
//                        .name(product.getName())
//                        .created_at(product.getCreated_at())
//                        .information(product.getInformation())
//                        .summary(product.getSummary())
//                        .price(product.getNor_infosEntity().getPrice())
//                        .build()
//        ).collect(Collectors.toList());
//
//    }

    //공부용 주석
    // 레포지토리의 메소드를 호출하여 조건에 맞는 product들을 list(products)에 저장
    // -> stream api를 이용하여 리스트를 스트림형으로 변환하고 각 상품에 대해 map연산 진행
    // -> AucProductResponseDto의 빌더패턴을 사용하여 새로운 AucProductResponseDto객체를 만듬
    // -> 상품의 이름을 AucProductResponseDto의 namme 필드에 설정
    // -> ...
    // -> 상품의 시작가격을 AucProductResponseDto의 start_price필드에 설정하는데 이는 Auc_infosEntity와 연관있음
    // -> ...
    // -> AucProductResponseDto객체를 빌드화함
    // -> 최종적으로 변환된 AucProductResponseDto객체들을 리스트로 모아 반환

    //위 4개의 서비스에서 iswish를 가져와야하는데 아직 로직을 고민중...
    public List<AucsProductResponseDto> getAllAucsHandProducts() {
        List<ProductsEntity> products = productsRepository.findByCategoryAndKind("AUCS", "HAND");
        if (products.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        return products.stream().map(product ->
                AucsProductResponseDto.builder()
                        .name(product.getName())
                        .createdTime(product.getCreatedTime())
                        .information(product.getInformation())
                        .summary(product.getSummary())
                        .brand(product.getBrand())
                        .start_price(product.getAucs_infosEntity().getStart_price())
                        .end(product.getAucs_infosEntity().getEnd())
                        .bids_code(product.getAucs_infosEntity().getBids_code())
                        .build()
        ).collect(Collectors.toList());
    }

    public List<AucsProductResponseDto> getAllAucsNormProducts() {
        List<ProductsEntity> products = productsRepository.findByCategoryAndKind("AUCS", "NORM");
        if (products.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        return products.stream().map(product ->
                AucsProductResponseDto.builder()
                        .name(product.getName())
                        .createdTime(product.getCreatedTime())
                        .information(product.getInformation())
                        .summary(product.getSummary())
                        .brand(product.getBrand())
                        .start_price(product.getAucs_infosEntity().getStart_price())
                        .end(product.getAucs_infosEntity().getEnd())
                        .bids_code(product.getAucs_infosEntity().getBids_code())
                        .build()
        ).collect(Collectors.toList());
    }

    public List<SaleProductResponseDto> getAllSaleHandProducts() {
        List<ProductsEntity> products = productsRepository.findByCategoryAndKind("SALE", "HAND");
        if (products.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        return products.stream().map(product ->
                SaleProductResponseDto.builder()
                        .name(product.getName())
                        .createdTime(product.getCreatedTime())
                        .information(product.getInformation())
                        .summary(product.getSummary())
                        .brand(product.getBrand())
                        .price(product.getSale_infosEntity().getPrice())
                        .build()
        ).collect(Collectors.toList());
    }

    public List<SaleProductResponseDto> getAllSaleNormProducts() {
        List<ProductsEntity> products = productsRepository.findByCategoryAndKind("SALE", "NORM");
        if (products.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        return products.stream().map(product ->
                SaleProductResponseDto.builder()
                        .name(product.getName())
                        .createdTime(product.getCreatedTime())
                        .information(product.getInformation())
                        .summary(product.getSummary())
                        .brand(product.getBrand())
                        .price(product.getSale_infosEntity().getPrice())
                        .build()
        ).collect(Collectors.toList());
    }


    @Override
    public void registerProduct(ProductRegisterRequestDto dto) {
        //상품 등록 서비스 로직

        // member-service로부터 이메일을 가져옴
        String emailFromMemberService = memberServiceClient.getEmail();

        //ProductsEntity를 먼저 저장을 한다.
        ProductsEntity product = ProductsEntity.builder()
                .name(dto.getName())
                .category(dto.getCategory())
                .information(dto.getInformation())
                .summary(dto.getSummary())
                .brand(dto.getBrand())
                .email(emailFromMemberService) // 가져온 이메일을 설정
                .build();
        // 'createdTime'이 자동으로 설정될 것이므로 필요 x

        //이미지 저장
        if(dto.getImages() != null && dto.getImages().size() <= 10) { // 이미지가 10개 이하인지 확인
            for(MultipartFile image : dto.getImages()) {
                String imageUrl = s3Utils.uploadFiles(image, "product-images"); // S3에 이미지 업로드 후 URL 반환
                ProductImgEntity imageEntity = ProductImgEntity.builder()
                        .url(imageUrl)
                        .build();
                product.addImage(imageEntity);
            }
        }

        productsRepository.save(product);

        //이후 경매인지 비경매인지 체크를 한 후 추가적으로 저장한다.
        if ("AUCS".equals(dto.getCategory())) {
            Aucs_infosEntity aucInfo = Aucs_infosEntity.builder()
                    .start_price(dto.getStart_price())
                    .end(dto.getEnd())
                    .bids_code(dto.getBids_code())
                    .productsEntity(product)
                    .build();

            aucs_infosRepository.save(aucInfo);

        } else if ("SALE".equals(dto.getCategory())) {
            Sale_infosEntity norInfo = Sale_infosEntity.builder()
                    .price(dto.getPrice())
                    .productsEntity(product)
                    .build();

            sale_infosRepository.save(norInfo);
        }
    }

    @Override
    public ProductSearchResponseDto searchProductByName(String name, String email) {
        //상품 검색 결과 반환
        //현재 기술적 한개로 검색시 정확히 일치하는 단 한개만을 보여줄 수 있음


        ProductsEntity product = productsRepository.findByName(name);

        //검색결과 -> 일치하는 것 없음
        if (product == null) {
            throw new AppException(ErrorCode.SEARCH_NOT_FOUND);
        }

        boolean userWishStatus = false; //기본값

        //검색결과 -> 일치하는 것 발견

        // member-service에서 사용자의 찜 여부를 확인
        List<Long> wishedProductIds = memberServiceClient.getWishesProductIdsByEmail(email);
        userWishStatus = wishedProductIds.contains(product.getProducts_id());

        //아래와 같이 짜면 경매/비경매 상품 따로 못보여줌
//            return ProductSearchResponseDto.builder()
//                    .name(product.getName())
//                    .createdTime(product.getCreatedTime())
//                    .summary(product.getSummary())
//                    .brand(product.getBrand())
//                    .isWished(userWishStatus)
//                    .build();

        //객체 생성 과정에서 선택적으로 특정 필드를 생성하기 위해 다음과 같이 코드 구성 -> .build가 나중에 나옴(return시에)
        ProductSearchResponseDto.ProductSearchResponseDtoBuilder responseDtoBuilder = ProductSearchResponseDto.builder()
                .name(product.getName())
                .createdTime(product.getCreatedTime())
                .summary(product.getSummary())
                .brand(product.getBrand())
                .isWished(userWishStatus);
        if("AUCS".equals(product.getCategory())){

            //최고가를 얻기 위해 nowPrice의 모든 값 비교
            List<Long> nowPrices = shippingServiceClient.getNowPricesByProductId(product.getProducts_id());
            Long highestPrice = nowPrices.stream().max(Long::compareTo).orElse(null);

            responseDtoBuilder
                    .start_price(product.getAucs_infosEntity().getStart_price())
                    .end(product.getAucs_infosEntity().getEnd())
                    .high(highestPrice);

        }
        else if("SALE".equals(product.getCategory())){
            responseDtoBuilder
                    .price(product.getSale_infosEntity().getPrice());
        }
        return responseDtoBuilder.build();

    }

    @Override
    public ProductDetailResponseDto getProductDetail(String product_code) {

        //단일 상품에 대한 상품의 상세 정보를 제공

        //추후 posts, comments도 여기서 제공하게 할 수도 있음

        ProductsEntity product = productsRepository.findByProductsCode(product_code);

        if (product == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        ProductDetailResponseDto.ProductDetailResponseDtoBuilder builder = ProductDetailResponseDto.builder()
                .name(product.getName())
                .category(product.getCategory())
                .createdTime(product.getCreatedTime())
                .information(product.getInformation())
                .summary(product.getSummary())
                .brand(product.getBrand());

        // 경매 상품 추가정보
        if ("auc".equals(product.getCategory()) && product.getAucs_infosEntity() != null) {
            builder.start_price(product.getAucs_infosEntity().getStart_price())
                    .end(product.getAucs_infosEntity().getEnd())
                    .bids_code(product.getAucs_infosEntity().getBids_code());
        }

        // 비경매 상품 추가정보
        if ("nor".equals(product.getCategory()) && product.getSale_infosEntity() != null) {
            builder.price(product.getSale_infosEntity().getPrice());
        }

        return builder.build();


        //게시판 정보들은 따로 보내준다

    }

}
