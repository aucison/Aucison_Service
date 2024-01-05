package com.example.aucison_service.elastic;




import com.example.aucison_service.jpa.product.ProductsEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


// Elasticsearch용 Repository 인터페이스 생성
@Repository
public interface ProductsSearchRepository extends ElasticsearchRepository<ProductsEntity, String> {
    //List<ProductsEntity> findByName(String name);

    // 유사도 검색을 위한 메소드
    List<ProductsEntity> findBySimilarName(String name);

}