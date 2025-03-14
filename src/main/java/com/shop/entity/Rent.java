package com.shop.entity;


import com.shop.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "rent")
public class Rent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rent_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rental_item_id")
    private RentalItem rentalItem; // 경매 아이템

    private int rentAmount;

    private LocalDateTime rentTime; // 응찰 시간

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // Enum 값을 문자열로 저장
    private PaymentStatus status; // 결제 상태 (PAID, CANCEL)

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member; // 응찰한 사용자(Member 엔티티 사용)


}
