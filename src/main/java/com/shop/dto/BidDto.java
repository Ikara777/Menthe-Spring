package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidDto {
    private Long itemId;      // 경매 아이템 ID
    private int bidAmount;   // 응찰 금액
    private Integer maxAmount;

    public BidDto(Long itemId, int bidAmount, Integer maxAmount){
        this.itemId = itemId;
        this.bidAmount = bidAmount;
        this.maxAmount = maxAmount;
    }
}
