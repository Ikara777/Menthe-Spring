package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EventDto {
    private Long id;
    private String title;

    // 시간을 담을수 있는 데이터 자료형
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemSellStatus itemSellStatus;

    public EventDto(Long id, String title, LocalDateTime start, LocalDateTime end, ItemSellStatus itemSellStatus) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
        this.itemSellStatus = itemSellStatus;
    }
}