package com.shop.dto;

import com.shop.constant.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RentDto {
    private Long itemId;      // 경매 아이템 ID
    private Long rentAmount;
    private PaymentStatus paymentStatus;

    public RentDto(Long itemId, Long rentAmount,PaymentStatus paymentStatus) {
        this.itemId = itemId;
        this.rentAmount = rentAmount;
        this.paymentStatus = paymentStatus;
    }
}
