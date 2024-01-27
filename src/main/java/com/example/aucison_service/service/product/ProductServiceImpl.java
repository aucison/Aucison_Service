package com.example.aucison_service.service.product;




import com.example.aucison_service.dto.aucs_sale.AucsProductResponseDto;
import com.example.aucison_service.dto.aucs_sale.SaleProductResponseDto;
import com.example.aucison_service.dto.product.ProductDetailResponseDto;
import com.example.aucison_service.dto.product.ProductRegisterFinshResponseDto;
import com.example.aucison_service.dto.product.ProductRegisterRequestDto;
import com.example.aucison_service.dto.product.UpdateOnlyCostResponseDto;
import com.example.aucison_service.dto.search.ProductSearchResponseDto;
import com.example.aucison_service.elastic.ProductsDocument;
import com.example.aucison_service.enums.Category;
import com.example.aucison_service.enums.Kind;
import com.example.aucison_service.enums.OrderType;
import com.example.aucison_service.enums.PStatusEnum;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.member.entity.HistoriesEntity;
import com.example.aucison_service.jpa.member.entity.HistoriesImgEntity;
import com.example.aucison_service.jpa.member.entity.MembersInfoEntity;
import com.example.aucison_service.jpa.member.repository.*;
import com.example.aucison_service.jpa.product.entity.*;
import com.example.aucison_service.jpa.product.repository.AucsInfosRepository;
import com.example.aucison_service.jpa.product.repository.BidCountsRepository;
import com.example.aucison_service.jpa.product.repository.ProductsRepository;
import com.example.aucison_service.jpa.product.repository.SaleInfosRepository;
import com.example.aucison_service.service.member.MemberDetails;
import com.example.aucison_service.service.s3.S3Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    ProductsRepository productsRepository;
    SaleInfosRepository sale_infosRepository;
    AucsInfosRepository aucs_infosRepository;
    MembersRepository membersRepository;
    WishesRepository wishesRepository;
    HistoriesRepository historiesRepository;
    HistoriesImgRepository historiesImgRepository;
    MembersInfoRepository membersInfoRepository;

    BidCountsRepository bidCountsRepository;
    S3Service s3Service;

    ElasticsearchOperations elasticsearchOperations;


    @Autowired
    public ProductServiceImpl(ProductsRepository productsRepository, SaleInfosRepository sale_infosRepository,
                              AucsInfosRepository aucs_infosRepository, MembersRepository membersRepository,
                              WishesRepository wishesRepository, BidCountsRepository bidCountsRepository,
                              HistoriesRepository historiesRepository, HistoriesImgRepository historiesImgRepository,
                              MembersInfoRepository membersInfoRepository,
                              S3Service s3Service,
                              ElasticsearchOperations elasticsearchOperations){
        this.productsRepository=productsRepository;
        this.aucs_infosRepository=aucs_infosRepository;
        this.sale_infosRepository=sale_infosRepository;
        this.membersRepository=membersRepository;
        this.wishesRepository=wishesRepository;
        this.bidCountsRepository = bidCountsRepository;
        this.historiesRepository = historiesRepository;
        this.historiesImgRepository = historiesImgRepository;
        this.membersInfoRepository = membersInfoRepository;
        this.s3Service=s3Service;
        this.elasticsearchOperations = elasticsearchOperations;
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
            throw new AppException(ErrorCode.PRODUCT_NOT_EXIST);
        }

        return products.stream()
                .peek(product -> {
                    // AucsInfosEntity가 null인 경우 엔티티를 새로고침
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

                    Long wishCount = wishesRepository.countByProductId(product.getProductsId());    //찜 집계

                    BidCountsEntity bidCountsEntity = bidCountsRepository.findByProductsId(product.getProductsId());

                    return AucsProductResponseDto.builder()
                            .productsId(product.getProductsId())
                            .name(product.getName())
                            .pStatus(product.getPStatus())
                            .startPrice(aucsInfo.getStartPrice())
                            .end(aucsInfo.getEnd())
                            .bidsCode(aucsInfo.getBidsCode())
                            .imageUrl(firstImageUrl) // 이미지 URL 목록 추가
                            .wishCount(wishCount) // 찜 횟수 추가
                            .totCnt(bidCountsEntity.getTotCnt())
                            .build();
                })
                .collect(Collectors.toList());
    }


    //모든 경매(AUCS) + 일반(NORM) 상품 반환
    @Transactional(readOnly = true)
    public List<AucsProductResponseDto> getAllAucsNormProducts() {
        List<ProductsEntity> products = productsRepository.findByCategoryAndKind("AUCS", "NORM");
        if (products.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXIST);
        }

        return products.stream().map(product -> {
            AucsInfosEntity aucsInfo = product.getAucsInfosEntity();

            // 첫 번째 이미지 URL 추출 (이미지가 없는 경우 null이 될 수 있음)
            String firstImageUrl = product.getImages().stream()
                    .map(ProductImgEntity::getUrl)
                    .findFirst()
                    .orElse(null);

            Long wishCount = wishesRepository.countByProductId(product.getProductsId());    //찜 집계

            BidCountsEntity bidCountsEntity = bidCountsRepository.findByProductsId(product.getProductsId());

            return AucsProductResponseDto.builder()
                    .productsId(product.getProductsId())
                    .name(product.getName())
                    .pStatus(product.getPStatus())
                    .startPrice(aucsInfo.getStartPrice())
                    .end(aucsInfo.getEnd())
                    .bidsCode(aucsInfo.getBidsCode())
                    .imageUrl(firstImageUrl) // 이미지 URL 목록 추가
                    .wishCount(wishCount) // 찜 횟수 추가
                    .totCnt(bidCountsEntity.getTotCnt())
                    .build();
        }).collect(Collectors.toList());
    }


    //모든 비경매(SALE) + 핸드메이드(HAND) 상품 반환
    @Transactional(readOnly = true)
    public List<SaleProductResponseDto> getAllSaleHandProducts() {
        List<ProductsEntity> products = productsRepository.findByCategoryAndKind("SALE", "HAND");
        if (products.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        return products.stream().map(product -> {
            SaleInfosEntity saleInfo = product.getSaleInfosEntity();

            // 첫 번째 이미지 URL 추출 (이미지가 없는 경우 null이 될 수 있음)
            String firstImageUrl = product.getImages().stream()
                    .map(ProductImgEntity::getUrl)
                    .findFirst()
                    .orElse(null);

            Long wishCount = wishesRepository.countByProductId(product.getProductsId());    //찜 집계

            return SaleProductResponseDto.builder()
                    .productsId(product.getProductsId())
                    .name(product.getName())
                    .pStatus(product.getPStatus())
                    .price(saleInfo.getPrice())
                    .imageUrl(firstImageUrl) // 이미지 URL 목록 추가
                    .wishCount(wishCount) // 찜 횟수 추가
                    .build();
        }).collect(Collectors.toList());
    }

    //모든 비경매(SALE) + 일반(NORM) 상품 반환
    @Transactional(readOnly = true)
    public List<SaleProductResponseDto> getAllSaleNormProducts() {
        List<ProductsEntity> products = productsRepository.findByCategoryAndKind("SALE", "NORM");
        if (products.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        return products.stream().map(product -> {
            SaleInfosEntity saleInfo = product.getSaleInfosEntity();

            // 첫 번째 이미지 URL 추출 (이미지가 없는 경우 null이 될 수 있음)
            String firstImageUrl = product.getImages().stream()
                    .map(ProductImgEntity::getUrl)
                    .findFirst()
                    .orElse(null);

            Long wishCount = wishesRepository.countByProductId(product.getProductsId());    //찜 집계

            return SaleProductResponseDto.builder()
                    .productsId(product.getProductsId())
                    .name(product.getName())
                    .pStatus(product.getPStatus())
                    .price(saleInfo.getPrice())
                    .imageUrl(firstImageUrl) // 이미지 URL 목록 추가
                    .wishCount(wishCount) // 찜 횟수 추가
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


        //아래 코드로 변경
        String email = principal.getMember().getEmail();

        //ProductsEntity를 먼저 저장을 한다.
        ProductsEntity product = ProductsEntity.builder()
                .name(dto.getName())
                .kind(dto.getKind())
                .category(dto.getCategory())
                .information(dto.getInformation())
                .tags(dto.getTags())
                .pStatus(PStatusEnum.S000)  //enum은 이렇게함
                .email(email) //  OAuth2 인증을 통해 가져온 이메일 설정
                .build();
        // 'createdTime'이 자동으로 설정될 것이므로 필요 x

        // 이미지 저장
        List<MultipartFile> images = dto.getImages();
        String firstImageUrl = null; // 첫 번째 이미지의 URL을 저장하기 위한 변수
        if (images != null && !images.isEmpty() && images.size() <= 10) {
            boolean isFirstImage = true;
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String imageUrl = s3Service.uploadFileAndGetUrl(file, "product");
                    ProductImgEntity productImg = ProductImgEntity.builder()
                            .url(imageUrl)
                            .product(product)
                            .build();
                    product.addImage(productImg); // 상품 엔티티에 이미지 추가

                    if (isFirstImage) {
                        firstImageUrl = imageUrl; // 첫 번째 이미지 URL 저장
                        isFirstImage = false;
                    }
                }
            }
        }

        productsRepository.save(product);

        //기본적으로 낙찰자 수 0 셋팅
        BidCountsEntity bidCount = BidCountsEntity.builder()
                .productsId(product.getProductsId())
                .totCnt(0)
                .build();

        bidCountsRepository.save(bidCount);


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

         // HistoriesEntity 생성 및 저장
        MembersInfoEntity membersInfo = membersInfoRepository.findByMembersEntity(principal.getMember());

        HistoriesEntity history = HistoriesEntity.builder()
                .orderType(OrderType.SELL)
                .category(dto.getCategory())
                .kind(dto.getKind())
                .productsId(product.getProductsId())
                .productName(dto.getName())
                .productDetail(dto.getInformation())
                .price(dto.getPrice()) // 가격 설정
                .membersInfoEntity(membersInfo)
                .build();
        historiesRepository.save(history);

        if (firstImageUrl != null) { // 첫 번째 이미지가 존재하는 경우
            HistoriesImgEntity historyImg = HistoriesImgEntity.builder()
                    .url(firstImageUrl)
                    .historiesEntity(history)
                    .build();
            historiesImgRepository.save(historyImg);
        } else {
            // 이미지가 없는 경우 처리 (예: null 또는 기본 이미지 URL 설정)
            HistoriesImgEntity historyImg = HistoriesImgEntity.builder()
                    .url(null)
                    .historiesEntity(history)
                    .build();
            historiesImgRepository.save(historyImg);
        }

    }

    @Override
    public UpdateOnlyCostResponseDto updateOnlyCost(Long productId, @AuthenticationPrincipal MemberDetails principal){
        ProductsEntity product = productsRepository.findByProductsId(productId);

        if (product == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        UpdateOnlyCostResponseDto.UpdateOnlyCostResponseDtoBuilder dto = UpdateOnlyCostResponseDto.builder();

        if("AUCS".equals(product.getCategory())){

        } else if("SALE".equals(product.getCategory())){
            dto.nowPrice(product.getSaleInfosEntity().getPrice());  //일반 상품이면 등록가격 다시 가져오면 됨

        }
        return dto.build();

    }


    @Override
    public ProductRegisterFinshResponseDto finshReisterProduct(Long productId, @AuthenticationPrincipal MemberDetails principal) {
        //상품등록 완료 후 확인 페이지 -> 솔직히 프론트 처리가 더 이상적일듯, 이전 페이지 내용 가져오면됨

        if (principal == null) {
            logger.info("인증되지 않은 사용자입니다!");
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        ProductsEntity product = productsRepository.findByProductsId(productId);

        String email = principal.getMember().getEmail();

        // 첫 번째 이미지 URL 추출 (이미지가 없는 경우 null이 될 수 있음)
        String firstImageUrl = product.getImages().stream()
                .map(ProductImgEntity::getUrl)
                .findFirst()
                .orElse(null);

        ProductRegisterFinshResponseDto.ProductRegisterFinshResponseDtoBuilder builder =ProductRegisterFinshResponseDto.builder()
                .name(product.getName())
                .kind(product.getKind())
                .category(product.getCategory())
                .tags(product.getTags())
                .email(email)
                .image(firstImageUrl);

        if("AUCS".equals(product.getCategory())){
            AucsInfosEntity aucsInfo = product.getAucsInfosEntity();
            builder.startPrice(aucsInfo.getStartPrice())
                    .end(aucsInfo.getEnd());

        } else if("SALE".equals(product.getCategory())){
            SaleInfosEntity saleInfo = product.getSaleInfosEntity();
            builder.price(saleInfo.getPrice());
        }

        return builder.build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSearchResponseDto> searchProductByName(String name) {
        Pageable pageable = PageRequest.of(0, 3); // 예시: 첫 페이지에 3개 반환
        List<ProductsDocument> products = findBySimilarName(name, pageable);


        if (products.isEmpty()) {
            throw new AppException(ErrorCode.SEARCH_NOT_FOUND);
        }


        //ProductSearchResponseDto 클래스에 Lombok의 @Builder 어노테이션이 적용되어 있을 떄  아래처럼 한다.
        return products.stream().map(product -> {
            Long wishCount = wishesRepository.countByProductId(product.getProductsId());    //찜 집계

            ProductSearchResponseDto.ProductSearchResponseDtoBuilder builder = ProductSearchResponseDto.builder()
                    .productsId(product.getProductsId())
                    .name(product.getName())
                    .pStatus(product.getPStatus())
                    .images(product.getImages()) // 이미지 URL 목록 추가
                    .wishCount(wishCount); // 찜 횟수 추가

            if ("AUCS".equals(product.getCategory())) {
                LocalDateTime aucEnd = LocalDateTime.ofInstant(Instant.ofEpochMilli(product.getAucEnd()), ZoneId.systemDefault());
                builder.end(aucEnd) // 변환된 LocalDateTime 할당
                        .high(product.getAucStartPrice());
            } else if ("SALE".equals(product.getCategory())) {
                builder.price(product.getSalePrice());
            }

            return builder.build();
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductsDocument> findBySimilarName(String name, Pageable pageable) {
        // 쿼리 구문 로깅
        String queryString = String.format("name:*%s*", name);
        logger.info("Executing search with query string: {}", queryString);

        // Elasticsearch 쿼리 생성
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.queryStringQuery(queryString))
                .withPageable(pageable)
                .build();

        try {
            // Elasticsearch 검색 실행
            SearchHits<ProductsDocument> searchHits = elasticsearchOperations.search(searchQuery, ProductsDocument.class);

            // 결과 로깅
            logger.info("Search hits: {}", searchHits.getTotalHits());

            // 검색 결과 처리 및 반환
            return searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .peek(document -> logger.info("Found document with ID: {}", document.getId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // 예외 로깅
            logger.error("Error occurred during search: {}", e.getMessage(), e);
            throw e;
        }
    }


    @Override
    @Transactional(readOnly = true)
    public ProductDetailResponseDto getProductDetail(Long productsId, @AuthenticationPrincipal MemberDetails principal) {
        ProductsEntity product = productsRepository.findByProductsId(productsId);

        if (product == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        Long wishCount = wishesRepository.countByProductId(productsId); //찜 집계

        BidCountsEntity bidCount = bidCountsRepository.findByProductsId(productsId);


        ProductDetailResponseDto.ProductDetailResponseDtoBuilder dto = ProductDetailResponseDto.builder()
                .name(product.getName())
                .kind(product.getKind())
                .category(product.getCategory())
                .information(product.getInformation())
                .pStatus(product.getPStatus())
                .tags(product.getTags())
                .email(product.getEmail())
                .wishCount(wishCount); // 찜 횟수 추가;

        // 경매 상품 추가정보
        if ("AUCS".equals(product.getCategory()) && product.getAucsInfosEntity() != null) {
            dto.startPrice(product.getAucsInfosEntity().getStartPrice())
                    .end(product.getAucsInfosEntity().getEnd())
                    .high(product.getAucsInfosEntity().getStartPrice())
                    .totCnt(bidCount.getTotCnt());

        }

        // 비경매 상품 추가정보
        if ("SALE".equals(product.getCategory()) && product.getSaleInfosEntity() != null) {
            dto.price(product.getSaleInfosEntity().getPrice());
        }

        return dto.build();

        //게시판 정보들은 따로 보내준다
    }

}
