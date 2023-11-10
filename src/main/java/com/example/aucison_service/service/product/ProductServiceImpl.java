package com.example.aucison_service.service.product;



import com.example.aucison_service.controller.AuthController;
import com.example.aucison_service.dto.aucs_sale.AucsProductResponseDto;
import com.example.aucison_service.dto.aucs_sale.SaleProductResponseDto;
import com.example.aucison_service.dto.product.ProductRegisterRequestDto;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.member.MembersRepository;
import com.example.aucison_service.jpa.member.WishesRepository;
import com.example.aucison_service.jpa.product.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    ProductsRepository productsRepository;
    SaleInfosRepository sale_infosRepository;
    AucsInfosRepository aucs_infosRepository;
    MembersRepository membersRepository;
    WishesRepository wishesRepository;




    @Autowired
    public ProductServiceImpl(ProductsRepository productsRepository, SaleInfosRepository sale_infosRepository,
                              AucsInfosRepository aucs_infosRepository, MembersRepository membersRepository,
                              WishesRepository wishesRepository){
        this.productsRepository=productsRepository;
        this.aucs_infosRepository=aucs_infosRepository;
        this.sale_infosRepository=sale_infosRepository;
        this.membersRepository=membersRepository;
        this.wishesRepository=wishesRepository;
    }


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


    //현재 이하 4개의 서비스에 N+1문제 발생 가능성 높음 -> 곧 해결 예정
    //모든 경매(AUCS) + 핸드메이드(HAND) 상품 반환
    public List<AucsProductResponseDto> getAllAucsHandProducts() {
        List<ProductsEntity> products = productsRepository.findByCategoryAndKind("AUCS", "HAND");
        if (products.isEmpty()) {
            logger.info("RODUCT_NOT_EXIST: 1");
            throw new AppException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        return products.stream().map(product ->
                AucsProductResponseDto.builder()
                        .name(product.getName())
                        .createdTime(product.getCreatedTime())
                        .information(product.getInformation())
                        .summary(product.getSummary())
                        .brand(product.getBrand())
                        .startPrice(product.getAucsInfosEntity().getStartPrice())
                        .end(product.getAucsInfosEntity().getEnd())
                        .bidsCode(product.getAucsInfosEntity().getBidsCode())
                        .build()
        ).collect(Collectors.toList());
    }


    //모든 경매(AUCS) + 일반(NORM) 상품 반환
    public List<AucsProductResponseDto> getAllAucsNormProducts() {
        List<ProductsEntity> products = productsRepository.findByCategoryAndKind("AUCS", "NORM");
        if (products.isEmpty()) {
            logger.info("RODUCT_NOT_EXIST: 2");
            throw new AppException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        return products.stream().map(product ->
                AucsProductResponseDto.builder()
                        .name(product.getName())
                        .createdTime(product.getCreatedTime())
                        .information(product.getInformation())
                        .summary(product.getSummary())
                        .brand(product.getBrand())
                        .startPrice(product.getAucsInfosEntity().getStartPrice())
                        .end(product.getAucsInfosEntity().getEnd())
                        .bidsCode(product.getAucsInfosEntity().getBidsCode())
                        .build()
        ).collect(Collectors.toList());
    }


    //모든 비경매(SALE) + 핸드메이드(HAND) 상품 반환
    public List<SaleProductResponseDto> getAllSaleHandProducts() {
        List<ProductsEntity> products = productsRepository.findByCategoryAndKind("SALE", "HAND");
        if (products.isEmpty()) {
            logger.info("RODUCT_NOT_EXIST: 3");
            throw new AppException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        return products.stream().map(product ->
                SaleProductResponseDto.builder()
                        .name(product.getName())
                        .createdTime(product.getCreatedTime())
                        .information(product.getInformation())
                        .summary(product.getSummary())
                        .brand(product.getBrand())
                        .price(product.getSaleInfosEntity().getPrice())
                        .build()
        ).collect(Collectors.toList());
    }

    //모든 비경매(SALE) + 일반(NORM) 상품 반환
    public List<SaleProductResponseDto> getAllSaleNormProducts() {
        List<ProductsEntity> products = productsRepository.findByCategoryAndKind("SALE", "NORM");
        if (products.isEmpty()) {
            logger.info("RODUCT_NOT_EXIST: 4");
            throw new AppException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        return products.stream().map(product ->
                SaleProductResponseDto.builder()
                        .name(product.getName())
                        .createdTime(product.getCreatedTime())
                        .information(product.getInformation())
                        .summary(product.getSummary())
                        .brand(product.getBrand())
                        .price(product.getSaleInfosEntity().getPrice())
                        .build()
        ).collect(Collectors.toList());
    }


    @Override
    public void registerProduct(ProductRegisterRequestDto dto, @AuthenticationPrincipal OAuth2User principal) {
        //상품 등록 서비스 로직

        if (principal == null) {
            logger.info("에러 발생함!");
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String email = principal.getAttribute("email");

        //ProductsEntity를 먼저 저장을 한다.
        ProductsEntity product = ProductsEntity.builder()
                .name(dto.getName())
                .category(dto.getCategory())
                .information(dto.getInformation())
                .summary(dto.getSummary())
                .brand(dto.getBrand())
                .email(email) //  OAuth2 인증을 통해 가져온 이메일 설정
                .build();
        // 'createdTime'이 자동으로 설정될 것이므로 필요 x

        //이미지 저장 -> 수정해야함
//        if(dto.getImages() != null && dto.getImages().size() <= 10) { // 이미지가 10개 이하인지 확인
//            for(MultipartFile image : dto.getImages()) {
//                String imageUrl = s3Utils.uploadFiles(image, "product-images"); // S3에 이미지 업로드 후 URL 반환
//                ProductImgEntity imageEntity = ProductImgEntity.builder()
//                        .url(imageUrl)
//                        .build();
//                product.addImage(imageEntity);
//            }
//        }

        productsRepository.save(product);

        //이후 경매인지 비경매인지 체크를 한 후 추가적으로 저장한다.
        if ("AUCS".equals(dto.getCategory())) {
            AucsInfosEntity aucInfo = AucsInfosEntity.builder()
                    .startPrice(dto.getStartPrice())
                    .end(dto.getEnd())
                    .bidsCode(dto.getBidsCode())
                    .productsEntity(product)
                    .build();

            aucs_infosRepository.save(aucInfo);

        } else if ("SALE".equals(dto.getCategory())) {
            SaleInfosEntity norInfo = SaleInfosEntity.builder()
                    .price(dto.getPrice())
                    .productsEntity(product)
                    .build();

            sale_infosRepository.save(norInfo);
        }
    }

//    @Override
//    public ProductSearchResponseDto searchProductByName(String name, String email) {
//        //상품 검색 결과 반환
//        //현재 기술적 한개로 검색시 정확히 일치하는 단 한개만을 보여줄 수 있음
//
//        //수정해야함
//        //ProductsEntity product = productsRepository.findByName(name);
//
//        //검색결과 -> 일치하는 것 없음
//        if (product == null) {
//            throw new AppException(ErrorCode.SEARCH_NOT_FOUND);
//        }
//
//        boolean userWishStatus = false; //기본값
//
//        //검색결과 -> 일치하는 것 발견
//
//        // member-service에서 사용자의 찜 여부를 확인
//        //사용자 조회 -> 수정해야함
//        //MembersEntity member = membersRepository.findByEmail(email);
//
//        // 사용자의 찜 목록 조회
//        //        List<Long> wishedProductIds = memberServiceClient.getWishesProductIdsByEmail(email);
//        List<WishesEntity> userWishes = wishesRepository.findByMembersEntity(member);
//        // 사용자의 찜 목록에서 상품 ID 확인
//        userWishStatus = userWishes.stream()
//                .anyMatch(wish -> wish.getProductId().equals(product.getProductsId()));
//
//        //아래와 같이 짜면 경매/비경매 상품 따로 못보여줌
////            return ProductSearchResponseDto.builder()
////                    .name(product.getName())
////                    .createdTime(product.getCreatedTime())
////                    .summary(product.getSummary())
////                    .brand(product.getBrand())
////                    .isWished(userWishStatus)
////                    .build();
//
//        //객체 생성 과정에서 선택적으로 특정 필드를 생성하기 위해 다음과 같이 코드 구성 -> .build가 나중에 나옴(return시에)
//        ProductSearchResponseDto.ProductSearchResponseDtoBuilder responseDtoBuilder = ProductSearchResponseDto.builder()
//                .name(product.getName())
//                .createdTime(product.getCreatedTime())
//                .summary(product.getSummary())
//                .brand(product.getBrand())
//                .isWished(userWishStatus);
//
//        /** TODO: msa 통신 부분 대체 필요
//         *
//        if("AUCS".equals(product.getCategory())){
//
//            //최고가를 얻기 위해 nowPrice의 모든 값 비교
//            List<Long> nowPrices = shippingServiceClient.getNowPricesByProductId(product.getProducts_id());
//            Long highestPrice = nowPrices.stream().max(Long::compareTo).orElse(null);
//
//            responseDtoBuilder
//                    .start_price(product.getAucs_infosEntity().getStart_price())
//                    .end(product.getAucs_infosEntity().getEnd())
//                    .high(highestPrice);
//
//        }
//        else if("SALE".equals(product.getCategory())){
//            responseDtoBuilder
//                    .price(product.getSale_infosEntity().getPrice());
//        }
//         */
//        return responseDtoBuilder.build();
//
//    }

//    @Override
//    public ProductDetailResponseDto getProductDetail(String product_code) {
//
//        //단일 상품에 대한 상품의 상세 정보를 제공
//
//        //추후 posts, comments도 여기서 제공하게 할 수도 있음
//
//        //수정해야함
//        //ProductsEntity product = productsRepository.findByProductsCode(product_code);
//
//        if (product == null) {
//            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
//        }
//
//        ProductDetailResponseDto.ProductDetailResponseDtoBuilder builder = ProductDetailResponseDto.builder()
//                .name(product.getName())
//                .category(product.getCategory())
//                .createdTime(product.getCreatedTime())
//                .information(product.getInformation())
//                .summary(product.getSummary())
//                .brand(product.getBrand());
//
//        // 경매 상품 추가정보
//        if ("auc".equals(product.getCategory()) && product.getAucsInfosEntity() != null) {
//            builder.startPrice(product.getAucsInfosEntity().getStartPrice())
//                    .end(product.getAucsInfosEntity().getEnd())
//                    .bidsCode(product.getAucsInfosEntity().getBidsCode());
//        }
//
//        // 비경매 상품 추가정보
//        if ("nor".equals(product.getCategory()) && product.getSaleInfosEntity() != null) {
//            builder.price(product.getSaleInfosEntity().getPrice());
//        }
//
//        return builder.build();
//
//
//        //게시판 정보들은 따로 보내준다
//
//    }

}
