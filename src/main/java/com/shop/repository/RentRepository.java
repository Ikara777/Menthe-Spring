package com.shop.repository;

import com.shop.constant.PaymentStatus;
import com.shop.entity.Member;
import com.shop.entity.Rent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RentRepository extends JpaRepository<Rent, Long> {


    @Query("SELECT SUM(r.rentAmount) FROM Rent r WHERE r.member.id = :memberId AND r.status = com.shop.constant.PaymentStatus.PAID")
    Optional<Integer> sumRentAmountByMemberId(@Param("memberId") Long memberId);

    List<Rent> findByMember(Member member);

}

