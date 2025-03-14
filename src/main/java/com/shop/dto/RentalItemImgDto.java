package com.shop.dto;

import com.shop.entity.RentalItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class RentalItemImgDto {
    private Long id;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repImgYn;
    private static ModelMapper modelMapper = new ModelMapper();


    public static RentalItemImgDto of(RentalItemImg rentalItemImg){
        return modelMapper.map(rentalItemImg, RentalItemImgDto.class);
    }
}

