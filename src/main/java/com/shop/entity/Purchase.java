package com.shop.entity;

import com.shop.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "purchase")
@Getter
@Setter
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "art_item_id", nullable = false)
    private ArtItem artItem;

    @Column(nullable = false)
    private int bidAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus; // PAID로 설정

    // 추가적인 정보 (주소, 전화번호 등)
    private String postcode;
    private String address;
    private String tel;

}
