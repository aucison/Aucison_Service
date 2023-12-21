package com.example.aucison_service.service.product;



import com.example.aucison_service.controller.AuthController;
import com.example.aucison_service.dto.aucs_sale.AucsProductResponseDto;
import com.example.aucison_service.dto.aucs_sale.SaleProductResponseDto;
import com.example.aucison_service.dto.product.ProductDetailResponseDto;
import com.example.aucison_service.dto.product.ProductRegisterRequestDto;
import com.example.aucison_service.dto.search.ProductSearchResponseDto;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.member.MembersEntity;
import com.example.aucison_service.jpa.member.MembersRepository;
import com.example.aucison_service.jpa.member.WishesRepository;
import com.example.aucison_service.jpa.product.*;
import com.example.aucison_service.service.member.MemberDetails;
import com.example.aucison_service.service.s3.S3Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    ProductsRepository productsRepository;
    SaleInfosRepository sale_infosRepository;
    AucsInfosRepository aucs_infosRepository;
    MembersRepository membersRepository;
    WishesRepository wishesRepository;
    S3Service s3Service;




    @Autowired
    public ProductServiceImpl(ProductsRepository productsRepository, SaleInfosRepository sale_infosRepository,
                              AucsInfosRepository aucs_infosRepository, MembersRepository membersRepository,
                              WishesRepository wishesRepository, S3Service s3Service){
        this.productsRepository=productsRepository;
        this.aucs_infosRepository=aucs_infosRepository;
        this.sale_infosRepository=sale_infosRepository;
        this.membersRepository=membersRepository;
        this.wishesRepository=wishesRepository;
        this.s3Service=s3Service;
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


    @PersistenceContext
    private EntityManager entityManager;
    //현재 이하 4개의 서비스에 N+1문제 발생 가능성 높음 -> 곧 해결 예정
    //모든 경매(AUCS) + 핸드메이드(HAND) 상품 반환
    @Transactional(readOnly = true)
    public List<AucsProductResponseDto> getAllAucsHandProducts() {
        List<ProductsEntity> products = productsRepository.findByCategoryAndKind("AUCS", "HAND");
        if (products.isEmpty()) {
            logger.info("PRODUCT_NOT_EXIST: 1");
            throw new AppException(ErrorCode.PRODUCT_NOT_EXIST);
        }

        return products.stream()
                .peek(product -> {
                    if (product.getAucsInfosEntity() == null) {
                        entityManager.refresh(product);
                    }
                })
                .map(product -> {
                    AucsInfosEntity aucsInfo = product.getAucsInfosEntity();

                    // 첫 번째 이미지 URL 추출 (이미지가 없는 경우 null이 될 수 있음)
                    String firstImageUrl = product.getImages().stream()
                            .map(ProductImgEntity::getUrl)
                            .findFirst()
                            .orElse(null);

                    return AucsProductResponseDto.builder()
                            .productsId(product.getProductsId())
                            .name(product.getName())
                            .information(product.getInformation())
                            .summary(product.getSummary())
                            .brand(product.getBrand())
                            .startPrice(aucsInfo.getStartPrice())
                            .end(aucsInfo.getEnd())
                            .bidsCode(aucsInfo.getBidsCode())
                            .imageUrl(firstImageUrl) // 이미지 URL 목록 추가
                            .build();
                })
                .collect(Collectors.toList());
    }


    //모든 경매(AUCS) + 일반(NORM) 상품 반환
    public List<AucsProductResponseDto> getAllAucsNormProducts() {
        List<ProductsEntity> products = productsRepository.findByCategoryAndKind("AUCS", "NORM");
        if (products.isEmpty()) {
            logger.info("RODUCT_NOT_EXIST: 2");
            throw new AppException(ErrorCode.PRODUCT_NOT_EXIST);
        }

        return products.stream().map(product -> {
            AucsInfosEntity aucsInfo = product.getAucsInfosEntity();

            // 첫 번째 이미지 URL 추출 (이미지가 없는 경우 null이 될 수 있음)
            String firstImageUrl = product.getImages().stream()
                    .map(ProductImgEntity::getUrl)
                    .findFirst()
                    .orElse(null);

            return AucsProductResponseDto.builder()
                    .productsId(product.getProductsId())
                    .name(product.getName())
                    .information(product.getInformation())
                    .summary(product.getSummary())
                    .brand(product.getBrand())
                    .startPrice(aucsInfo.getStartPrice())
                    .end(aucsInfo.getEnd())
                    .bidsCode(aucsInfo.getBidsCode())
                    .imageUrl(firstImageUrl) // 이미지 URL 목록 추가
                    .build();
        }).collect(Collectors.toList());
    }


    //모든 비경매(SALE) + 핸드메이드(HAND) 상품 반환
    public List<SaleProductResponseDto> getAllSaleHandProducts() {
        List<ProductsEntity> products = productsRepository.findByCategoryAndKind("SALE", "HAND");
        if (products.isEmpty()) {
            logger.info("RODUCT_NOT_EXIST: 3");
            throw new AppException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        return products.stream().map(product -> {
            SaleInfosEntity saleInfo = product.getSaleInfosEntity();

            // 첫 번째 이미지 URL 추출 (이미지가 없는 경우 null이 될 수 있음)
            String firstImageUrl = product.getImages().stream()
                    .map(ProductImgEntity::getUrl)
                    .findFirst()
                    .orElse(null);

            return SaleProductResponseDto.builder()
                    .productsId(product.getProductsId())
                    .name(product.getName())
                    .information(product.getInformation())
                    .summary(product.getSummary())
                    .brand(product.getBrand())
                    .price(saleInfo.getPrice())
                    .imageUrl(firstImageUrl) // 이미지 URL 목록 추가
                    .build();
        }).collect(Collectors.toList());
    }

    //모든 비경매(SALE) + 일반(NORM) 상품 반환
    public List<SaleProductResponseDto> getAllSaleNormProducts() {
        List<ProductsEntity> products = productsRepository.findByCategoryAndKind("SALE", "NORM");
        if (products.isEmpty()) {
            logger.info("RODUCT_NOT_EXIST: 4");
            throw new AppException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        return products.stream().map(product -> {
            SaleInfosEntity saleInfo = product.getSaleInfosEntity();

            // 첫 번째 이미지 URL 추출 (이미지가 없는 경우 null이 될 수 있음)
            String firstImageUrl = product.getImages().stream()
                    .map(ProductImgEntity::getUrl)
                    .findFirst()
                    .orElse(null);

            return SaleProductResponseDto.builder()
                    .productsId(product.getProductsId())
                    .name(product.getName())
                    .information(product.getInformation())
                    .summary(product.getSummary())
                    .brand(product.getBrand())
                    .price(saleInfo.getPrice())
                    .imageUrl(firstImageUrl) // 이미지 URL 목록 추가
                    .build();
        }).collect(Collectors.toList());
    }


    //경매 코드 생성
    public String generateBidsCode() {
        return UUID.randomUUID().toString();
    }

    @Override
    @Transactional
    public void registerProduct(ProductRegisterRequestDto dto,@AuthenticationPrincipal MemberDetails principal) {
        //상품 등록 서비스 로직

        if (principal == null) {
            logger.info("인증되지 않은 사용자입니다!");
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

//        String email = principal.getAttribute("email");
        //아래 코드로 변경
        String email = principal.getMember().getEmail();

        //ProductsEntity를 먼저 저장을 한다.
        ProductsEntity product = ProductsEntity.builder()
                .name(dto.getName())
                .kind(dto.getKind())
                .category(dto.getCategory())
                .information(dto.getInformation())
                .summary(dto.getSummary())
                .brand(dto.getBrand())
                .email(email) //  OAuth2 인증을 통해 가져온 이메일 설정
                .build();
        // 'createdTime'이 자동으로 설정될 것이므로 필요 x

        // 이미지 저장
        List<MultipartFile> images = dto.getImages();
        if (images != null && !images.isEmpty() && images.size() <= 10) {
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String imageUrl = s3Service.uploadFileAndGetUrl(file, "product");
                    ProductImgEntity productImg = ProductImgEntity.builder()
                            .url(imageUrl)
                            .product(product)
                            .build();
                    product.addImage(productImg); // 상품 엔티티에 이미지 추가
                }
            }
        }

        productsRepository.save(product);


        //이후 경매인지 비경매인지 체크를 한 후 추가적으로 저장
        if ("AUCS".equals(dto.getCategory())) {
            String bidsCode = generateBidsCode(); // 고유한 bidsCode 생성
            AucsInfosEntity aucInfo = AucsInfosEntity.builder()
                    .startPrice(dto.getStartPrice())
                    .end(dto.getEnd())
                    .bidsCode(bidsCode)
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

    @Override
    public ProductSearchResponseDto searchProductByName(String name) {
        //상품 검색 결과 반환
        //현재 기술적 한개로 검색시 정확히 일치하는 단 한개만을 보여줄 수 있음


        ProductsEntity product = productsRepository.findByName(name);

        //검색결과 -> 일치하는 것 없음
        if (product == null) {
            throw new AppException(ErrorCode.SEARCH_NOT_FOUND);
        }


        //객체 생성 과정에서 선택적으로 특정 필드를 생성하기 위해 다음과 같이 코드 구성 -> .build가 나중에 나옴(return시에)
        ProductSearchResponseDto.ProductSearchResponseDtoBuilder dto = ProductSearchResponseDto.builder()
                .productsId(product.getProductsId())
                .name(product.getName())
                .summary(product.getSummary())
                .brand(product.getBrand());



        if("AUCS".equals(product.getCategory())){

//            //최고가를 얻기 위해 nowPrice의 모든 값 비교
//            List<Long> nowPrices = shippingServiceClient.getNowPricesByProductId(product.getProducts_id());
//            Long highestPrice = nowPrices.stream().max(Long::compareTo).orElse(null);

            dto
                    .end(product.getAucsInfosEntity().getEnd())
                    .high(product.getAucsInfosEntity().getStartPrice());    //임시로 최고가를 이딴식으로 가져옴

        }
        else if("SALE".equals(product.getCategory())){
            dto
                    .price(product.getSaleInfosEntity().getPrice());
        }


        return dto.build();

    }

    @Override
    public ProductDetailResponseDto getProductDetail(Long productsId) {


        ProductsEntity product = productsRepository.findByProductsId(productsId);

        if (product == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        ProductDetailResponseDto.ProductDetailResponseDtoBuilder builder = ProductDetailResponseDto.builder()
                .name(product.getName())
                .kind(product.getKind())
                .category(product.getCategory())
                .information(product.getInformation())
                .summary(product.getSummary())
                .brand(product.getBrand());

        // 경매 상품 추가정보
        if ("AUCS".equals(product.getCategory()) && product.getAucsInfosEntity() != null) {
            builder.startPrice(product.getAucsInfosEntity().getStartPrice())
                    .end(product.getAucsInfosEntity().getEnd())
                    .high(product.getAucsInfosEntity().getStartPrice());
        }

        // 비경매 상품 추가정보
        if ("SALE".equals(product.getCategory()) && product.getSaleInfosEntity() != null) {
            builder.price(product.getSaleInfosEntity().getPrice());
        }

        return builder.build();


        //게시판 정보들은 따로 보내준다

    }

}
