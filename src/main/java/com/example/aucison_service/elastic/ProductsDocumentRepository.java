package com.example.aucison_service.elastic;


import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;



// Elasticsearch용 Repository 인터페이스 생성
@Repository
public interface ProductsDocumentRepository extends ElasticsearchRepository<ProductsDocument, String> {
    //List<ProductsEntity> findByName(String name);

    // 유사도 검색을 위한 메소드
    //List<ProductsDocument> findBySimilarName(String name);

    <S extends ProductsDocument> S save(S entity);


}