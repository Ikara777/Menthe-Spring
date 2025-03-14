package com.shop.dto;

import com.shop.constant.PaymentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RentalDto {
    private Long id;
    private String name;
    private String imgUrl; // DTO에도 imgUrl 필드가 있어야 함
    private long totalCount;


    public RentalDto(Long id, String name, String imgUrl, long totalCount) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
        this.totalCount = totalCount;

    }

}
