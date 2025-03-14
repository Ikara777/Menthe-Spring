package com.shop.repository;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ArtItemRepository extends JpaRepository<ArtItem, Long>, QuerydslPredicateExecutor<ArtItem>, ItemRepositoryCustom {

    // 작가 이름으로 검색
    Page<ArtItem> findByArtistNameContaining(String artistName, Pageable pageable);

    // 작품 이름으로 검색
    Page<ArtItem> findByArtNameContaining(String artName, Pageable pageable);


    @Modifying
    @Transactional
    @Query("UPDATE ArtItem a SET a.eventStart = null, a.eventEnd = null, a.event = null WHERE a.event.id = :eventId")
    void clearEventStartAndEndForEventId(@Param("eventId") Long eventId);


    List<ArtItem> findAllByItemSellStatus(ItemSellStatus itemSellStatus);




    @Query("SELECT i FROM ArtItemImg i WHERE i.artItem.id = :artItemId ORDER BY i.id ASC")
    List<ArtItemImg> findImagesByArtItemId(@Param("ArtItemId") Long artItemId);



    List<ArtItem> findByEventStartBeforeAndEventEndAfter(LocalDateTime nowStart, LocalDateTime nowEnd);

    List<ArtItem> findByEventEndBefore(LocalDateTime now);

}
