package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MyBidDto {
    private String artName;       // 그림명
    private String artistName;    // 작가명
    private String imgUrl;        // 이미지 URL 추가
    private LocalDateTime bidTime; // 응찰시간
    private int maxAmount;        // 본인최고응찰가
    private int highestBidAmount; // 최고응찰가

    public MyBidDto(String artName, String artistName, String imgUrl, LocalDateTime bidTime, int maxAmount, int highestBidAmount) {
        this.artName = artName;
        this.artistName = artistName;
        this.imgUrl = imgUrl;
        this.bidTime = bidTime;
        this.maxAmount = maxAmount;
        this.highestBidAmount = highestBidAmount;
    }
}

