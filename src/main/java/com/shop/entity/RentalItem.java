package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.RentalItemDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name = "RentalItem")
@ToString
public class RentalItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rentalItem_id")
    private Long id;

    private String artistName;
    private String artName;
    private int price;

    @Column(length = 65535, columnDefinition = "TEXT") // 길이를 충분히 늘림
    private String imgUrl; // 이미지 URL 필드

    @Lob // 대용량 데이터 처리
    private String writerDesc; // 글자 수가 많은 필드

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; // 상품판매 상태


    public void updateRentalItem(RentalItemDto itemFormDto){
        this.id = itemFormDto.getId();
        this.artistName = itemFormDto.getArtistName();
        this.artName = itemFormDto.getArtName();
        this.price = itemFormDto.getPrice();
        this.imgUrl = itemFormDto.getImgUrl();
        this.writerDesc = itemFormDto.getWriterDesc();
        this.itemSellStatus = itemFormDto.getItemSellStatus();

        // 로그 출력----------
        System.out.println("----- Item 앤티티 업데이트 -----");
        System.out.println("작가 이름 : "+itemFormDto.getArtistName());
        System.out.println("작품 이름 : "+itemFormDto.getArtName());
        System.out.println("아이템 가격 : "+itemFormDto.getPrice());
        System.out.println("아이탬 번호 : "+itemFormDto.getId());
        System.out.println("아이템 상새 정보 : "+itemFormDto.getWriterDesc());
        System.out.println("아이템 판매 상태 : "+itemFormDto.getItemSellStatus());
        // 로그 출력----------

    }



}
