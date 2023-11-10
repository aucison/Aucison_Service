package com.example.aucison_service.jpa.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoriesImgRepository extends JpaRepository<HistoriesImgEntity, Long> {
    HistoriesImgEntity findByHistoriesEntity(HistoriesEntity historiesEntity);
}
