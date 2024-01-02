package com.example.aucison_service.jpa.product;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Elasticsearch용 Repository 인터페이스 생성
@Repository
public interface ProductsSearchRepository extends ElasticsearchRepository<ProductsEntity, String> {
    List<ProductsEntity> findByName(String name);
}