package com.shop.repository;

import com.shop.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 이메일과 merchant_uid로 합계 계산
    @Query("SELECT SUM(p.paidAmount) FROM Payment p WHERE p.buyerEmail = :email AND p.merchantUid = :merchantUid AND p.status = com.shop.constant.PaymentStatus.PAID")
    Optional<Integer> sumPaidAmountByEmailAndMerchantUid(@Param("email") String email, @Param("merchantUid") String merchantUid);

    Optional<Payment> findByImpUid(String impUid);

    @Query("SELECT p FROM Payment p WHERE p.buyerEmail = :buyerEmail AND p.merchantUid = :merchantUid")
    Page<Payment> findByBuyerEmailAndMerchantUidCustom(@Param("buyerEmail") String buyerEmail,
                                                       @Param("merchantUid") String merchantUid,
                                                       Pageable pageable);

}

