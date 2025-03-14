package com.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "bid")
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "art_item_id")
    private ArtItem artItem; // 경매 아이템

    private int bidAmount; // 응찰 금액

    private LocalDateTime bidTime; // 응찰 시간

    private Integer maxAmount;

    private boolean purchaseConfirmed = false; // 구매 확정 여부

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member; // 응찰한 사용자(Member 엔티티 사용)

}
