package com.shop.repository;

import com.shop.entity.ArtItemImg;
import com.shop.entity.RentalItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalItemImgRepository extends JpaRepository<RentalItemImg, Long> {

    List<RentalItemImg>     findByRentalItemIdOrderByIdAsc(Long rentalItemId);
    List<RentalItemImg>     findByRentalItemId(Long rentalItemId); // 특정 ArtItem의 이미지 리스트 조회

}
