package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchDto {
    private ItemSellStatus searchSellStatus; // 확인 필요
    private String searchBy;
    private String searchQuery = "";
}
