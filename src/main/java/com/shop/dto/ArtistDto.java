package com.shop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ArtistDto {
    private Long id;
    private String name;
    private String imgUrl; // DTO에도 imgUrl 필드가 있어야 함
    private long totalSales;

    public ArtistDto(Long id, String name, String imgUrl, long totalSales) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
        this.totalSales = totalSales;
    }


}

