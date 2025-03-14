package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.ArtItem;
import com.shop.entity.RentalItem;
import jakarta.validation.constraints.Min;
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
public class ArtItemDto {
    private Long id;

    @NotBlank(message = "작가명은 필수 입력 값입니다.")
    private String artistName;

    @NotBlank(message = "작품명은 필수 입력 값입니다.")
    private String artName;

    private String priceRange;

    @NotNull(message = "시작 가격은 필수 입력 값입니다.")
    private Integer startPrice;

    private String imgUrl; // 이미지 URL 필드

    private String writerDesc;

    private ItemSellStatus itemSellStatus;

    private List<ArtItemImgDto> artItemImgDtoList = new ArrayList<>(); //상품 이미지 정보 리스트

    private List<Long> itemImgIds = new ArrayList<>(); //상품 이미지 아이디


    // Entity -> DTO 변환 메서드
    // 1. 앤티티와 Dto 간 변환을 일관되게 처리함
    // 2. 서비스 클레스에서 엔티티를 Dto로 변환할 때 간단하게 ArtItemDto.of(ArtItem artItem)을 호출하여 가독성 높임
    // 3. 메서드 내부에서 모든 변환 로직을 캡슐화해 서비스 레이어가 간결해짐.
    // 4. 추가 로직 확장 가능
    public static ArtItemDto of(ArtItem artItem) {
        ArtItemDto artItemDto = new ArtItemDto();
        artItemDto.setId(artItem.getId());
        artItemDto.setArtistName(artItem.getArtistName());
        artItemDto.setArtName(artItem.getArtName());
        artItemDto.setPriceRange(artItem.getPriceRange());
        artItemDto.setStartPrice(artItem.getStartPrice());
        artItemDto.setImgUrl(artItem.getImgUrl());
        artItemDto.setWriterDesc(artItem.getWriterDesc());
        artItemDto.setItemSellStatus(artItem.getItemSellStatus());
        return artItemDto;
    }

    private static ModelMapper modelMapper = new ModelMapper();

    public ArtItem createArtItem(){
        return modelMapper.map(this,ArtItem.class);
    }

}
