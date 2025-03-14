package com.shop.repository;

import com.shop.entity.RentalItemImg;
import com.shop.entity.RentalItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RentalItemRepository extends JpaRepository<RentalItem, Long>, QuerydslPredicateExecutor<RentalItem>, ItemRepositoryCustom {

    // 작가 이름으로 검색
    Page<RentalItem> findByArtistNameContaining(String artistName, Pageable pageable);

    // 작품 이름으로 검색
    Page<RentalItem> findByArtNameContaining(String artName, Pageable pageable);

    RentalItem findAllById(Long ItemId);

    @Query("SELECT i FROM RentalItemImg i WHERE i.rentalItem.id = :rentalItemId ORDER BY i.id ASC")
    List<RentalItemImg> findImagesByRentalItemId(@Param("rentalItemId") Long rentalItemId);


}
