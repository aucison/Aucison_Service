package com.example.aucison_service.jpa.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoriesImgRepository extends JpaRepository<HistoriesImg, Long> {
    HistoriesImg findByHistories(Histories histories);  //구매/판매 내역 조회 entity로 해당 상품 이미지 조회
}
