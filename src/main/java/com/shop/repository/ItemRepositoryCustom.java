package com.shop.repository;

import com.shop.dto.ArtItemDto;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.RentalItemDto;
import com.shop.entity.ArtItem;
import com.shop.entity.RentalItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {
    Page<ArtItemDto> getArtItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
    Page<RentalItemDto> getRentalItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
    Page<RentalItem> getAdminRentalItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
    Page<ArtItem> getAdminArtItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

}

