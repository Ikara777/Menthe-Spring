package com.shop.entity;

import com.shop.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment") // 테이블 이름을 'payment'로 지정
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 ID
    private Long id;

    @Column(nullable = false, unique = true)
    private String impUid; // 포트원 결제 고유번호

    @Column(nullable = false)
    private String merchantUid; // 상점 고유 주문번호

    @Column(nullable = false)
    private int paidAmount; // 결제 금액

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // Enum 값을 문자열로 저장
    private PaymentStatus status; // 결제 상태 (PAID, CANCEL)

    private String buyerName; // 구매자 이름
    private String buyerEmail; // 구매자 이메일
    private String buyerTel; // 구매자 전화번호
    private String buyerAddr; // 구매자 주소

    private LocalDateTime paidAt; // 결제 완료 시간

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
