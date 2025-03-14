package com.shop.dto;

import com.shop.entity.ArtItemImg;
import com.shop.entity.RentalItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class ArtItemImgDto {
    private Long id;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repImgYn;
    private static ModelMapper modelMapper = new ModelMapper();


    public static ArtItemImgDto of(ArtItemImg artItemImg){
        return modelMapper.map(artItemImg, ArtItemImgDto.class);
    }
}

