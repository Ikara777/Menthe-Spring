package com.shop.repository;

import com.shop.entity.CashSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CashSummaryRepository extends JpaRepository<CashSummary, Long> {

        // 이메일로 사용자 데이터 조회
        Optional<CashSummary> findByEmail(String email);
        CashSummary findAllByEmail(String userEmail);
}