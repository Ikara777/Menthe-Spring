package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ArtItemDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "art_item")
public class ArtItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "art_id")
    private Long id;

    private String artistName;
    private String artName;
    private String priceRange;
    private int startPrice;

    @Column(length = 65535, columnDefinition = "TEXT") // 길이를 충분히 늘림
    private String imgUrl; // 이미지 URL 필드

    @Lob // 대용량 데이터 처리
    private String writerDesc; // 글자 수가 많은 필드

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; // 상품판매 상태

    private LocalDateTime eventEnd;

    private LocalDateTime eventStart;

    @ManyToOne
    @JoinColumn(name = "event_id") // 외래 키 컬럼명
    private Event event;


    public void updateArtItem(ArtItemDto itemFormDto){
        this.id = itemFormDto.getId();
        this.artistName = itemFormDto.getArtistName();
        this.artName = itemFormDto.getArtName();
        this.priceRange = itemFormDto.getPriceRange();
        this.startPrice = itemFormDto.getStartPrice();
        this.imgUrl = itemFormDto.getImgUrl();
        this.writerDesc = itemFormDto.getWriterDesc();
        this.itemSellStatus = itemFormDto.getItemSellStatus();

        // 로그 출력----------
        System.out.println("----- 아트 Item 앤티티 업데이트 -----");
        System.out.println("작가 이름 : "+itemFormDto.getArtistName());
        System.out.println("작품 이름 : "+itemFormDto.getArtName());
        System.out.println("아이템 가격 범위 : "+itemFormDto.getPriceRange());
        System.out.println("아이템 가격 : "+itemFormDto.getStartPrice());
        System.out.println("아이탬 번호 : "+itemFormDto.getId());
        System.out.println("아이템 상새 정보 : "+itemFormDto.getWriterDesc());
        System.out.println("아이템 판매 상태 : "+itemFormDto.getItemSellStatus());
        // 로그 출력----------

    }


}
