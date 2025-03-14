package com.shop.service;

import com.shop.entity.CashSummary;
import com.shop.entity.Member;
import com.shop.repository.CashSummaryRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class CashSummaryService {

    @Autowired
    private CashSummaryRepository cashSummaryRepository;

    // 캐시 잔액 저장 또는 업데이트
    @Transactional
    public void saveOrUpdateCashSummary(String email, String name, Integer totalAmount) {
        // 기존 데이터 확인
        Optional<CashSummary> optionalCashSummary = cashSummaryRepository.findByEmail(email);

        CashSummary cashSummary;
        if (optionalCashSummary.isPresent()) {
            // 기존 데이터 업데이트
            cashSummary = optionalCashSummary.get();
            cashSummary.setTotalAmount(totalAmount);
        } else {
            // 새로운 데이터 저장
            cashSummary = new CashSummary();
            cashSummary.setEmail(email);
            cashSummary.setName(name);
            cashSummary.setTotalAmount(totalAmount);
        }

        // 저장 또는 업데이트
        cashSummaryRepository.save(cashSummary);
    }

    public CashSummary getCashSummaryByEmail(String userEmail){
        CashSummary cashSummary = cashSummaryRepository.findAllByEmail(userEmail);
        if(cashSummary == null){
            throw new RuntimeException("로그인된 사용자를 찾을 수 없습니다.");
        }
        return cashSummary;
    }

}