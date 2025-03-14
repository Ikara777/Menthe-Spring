package com.shop.dto;


import com.shop.constant.ItemSellStatus;
import com.shop.entity.RentalItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RentalItemDto {

    private Long id;

    @NotBlank(message = "작가명은 필수 입력 값입니다.")
    private String artistName;

    @NotBlank(message = "작품명은 필수 입력 값입니다.")
    private String artName;

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price;

    private String imgUrl; // 이미지 URL 필드

    private String writerDesc;

    private ItemSellStatus itemSellStatus;

    private List<RentalItemImgDto> rentalItemImgDtoList = new ArrayList<>(); //상품 이미지 정보 리스트

    private List<Long> itemImgIds = new ArrayList<>(); //상품 이미지 아이디


    public RentalItemDto(Long id, String artistName, String artName, int price, String imgUrl, String writerDesc, ItemSellStatus itemSellStatus) {
        this.id = id;
        this.artistName = artistName;
        this.artName = artName;
        this.price = price;
        this.imgUrl = imgUrl;
        this.writerDesc = writerDesc;
        this.itemSellStatus = itemSellStatus;

    }


    public static RentalItemDto of(RentalItem rentalItem) {
        RentalItemDto rentalItemDto = new RentalItemDto();
        rentalItemDto.setId(rentalItem.getId());
        rentalItemDto.setArtistName(rentalItem.getArtistName());
        rentalItemDto.setArtName(rentalItem.getArtName());
        rentalItemDto.setPrice(rentalItem.getPrice());
        rentalItemDto.setImgUrl(rentalItem.getImgUrl());
        rentalItemDto.setWriterDesc(rentalItem.getWriterDesc());
        rentalItemDto.setItemSellStatus(rentalItem.getItemSellStatus());
        return rentalItemDto;
    }

    private static ModelMapper modelMapper = new ModelMapper();

    public RentalItem createRentalItem(){
        return modelMapper.map(this,RentalItem.class);
    }


}
