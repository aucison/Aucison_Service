package com.example.aucison_service.jpa.shipping.repository;

import com.example.aucison_service.jpa.shipping.entity.PageAccessLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageAccessLogsRepository extends JpaRepository<PageAccessLogs, Long> {
}
