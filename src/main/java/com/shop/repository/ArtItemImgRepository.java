package com.shop.repository;

import com.shop.entity.ArtItemImg;
import com.shop.entity.RentalItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtItemImgRepository extends JpaRepository<ArtItemImg, Long> {

    List<ArtItemImg> findByArtItemIdOrderByIdAsc(Long rentalItemId);
    List<ArtItemImg> findByArtItemId(Long artItemId); // 특정 ArtItem의 이미지 리스트 조회


}
