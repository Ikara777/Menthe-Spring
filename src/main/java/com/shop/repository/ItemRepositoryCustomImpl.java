package com.shop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ArtItemDto;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.RentalItemDto;
import com.shop.entity.ArtItem;
import com.shop.entity.QArtItem;
import com.shop.entity.QRentalItem;
import com.shop.entity.RentalItem;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@Slf4j
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 검색 키워드로 이름을 필터링하는 쿼리 생성
    private BooleanExpression artItemNmLike(String searchQuery) {
        return StringUtils.isEmpty(searchQuery) ? null : QArtItem.artItem.artName.contains(searchQuery);
    }

    private BooleanExpression searchArtItemSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus == null ? null : QArtItem.artItem.itemSellStatus.eq(searchSellStatus);
    }

    // 검색 키워드로 이름을 필터링하는 쿼리 생성
    private BooleanExpression rentalItemNmLike(String searchQuery) {
        return StringUtils.isEmpty(searchQuery) ? null : QRentalItem.rentalItem.artName.contains(searchQuery);
    }

    private BooleanExpression searchRentalItemSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus == null ? null : QRentalItem.rentalItem.itemSellStatus.eq(searchSellStatus);
    }

    @Override
    public Page<ArtItemDto> getArtItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QArtItem artItem = QArtItem.artItem;

        log.info("Executing getArtItemPage");
        log.info("Search query: {}", itemSearchDto.getSearchQuery());
        log.info("Sell status: {}", itemSearchDto.getSearchSellStatus());
        log.info("Pageable: {}", pageable);

        // 페이징 데이터를 가져오는 쿼리
        List<ArtItemDto> content = queryFactory
                .select(Projections.bean(
                        ArtItemDto.class,
                        artItem.id,
                        artItem.artistName,
                        artItem.artName,
                        artItem.priceRange,
                        artItem.startPrice,
                        artItem.imgUrl,
                        artItem.writerDesc,
                        artItem.itemSellStatus // 그대로 매핑
                ))
                .from(artItem)
                .where(
                        artItemNmLike(itemSearchDto.getSearchQuery()),
                        searchArtItemSellStatusEq(itemSearchDto.getSearchSellStatus())
                )
                .orderBy(artItem.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 개수 가져오는 쿼리
        long total = queryFactory
                .select(artItem.count())
                .from(artItem)
                .where(
                        artItemNmLike(itemSearchDto.getSearchQuery()),
                        searchArtItemSellStatusEq(itemSearchDto.getSearchSellStatus())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }


    @Override
    public Page<RentalItemDto> getRentalItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QRentalItem rentalItem = QRentalItem.rentalItem; // QueryDSL 엔티티

        log.info("Executing getRentalItemPage");
        log.info("Search query: {}", itemSearchDto.getSearchQuery());
        log.info("Sell status: {}", itemSearchDto.getSearchSellStatus());
        log.info("Pageable: {}", pageable);

        // 페이징 데이터를 가져오는 쿼리
        List<RentalItemDto> content = queryFactory
                .select(Projections.bean(
                        RentalItemDto.class,
                        rentalItem.id,
                        rentalItem.artistName,
                        rentalItem.artName,
                        rentalItem.price,
                        rentalItem.imgUrl,
                        rentalItem.writerDesc,
                        rentalItem.itemSellStatus
                ))
                .from(rentalItem)
                .where(
                        rentalItemNmLike(itemSearchDto.getSearchQuery()), // 검색 조건
                        searchRentalItemSellStatusEq(itemSearchDto.getSearchSellStatus()) // 판매 상태 조건
                )
                .orderBy(rentalItem.id.desc()) // 최신순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 개수 가져오는 쿼리
        long total = queryFactory
                .select(rentalItem.count())
                .from(rentalItem)
                .where(
                        rentalItemNmLike(itemSearchDto.getSearchQuery()),
                        searchRentalItemSellStatusEq(itemSearchDto.getSearchSellStatus())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }





    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return null; // 검색어가 없으면 조건 추가 안 함
        }

        if ("artistName".equalsIgnoreCase(searchBy)) {
            return QRentalItem.rentalItem.artistName.containsIgnoreCase(searchQuery); // 작가 이름 검색
        } else if ("artName".equalsIgnoreCase(searchBy)) {
            return QRentalItem.rentalItem.artName.containsIgnoreCase(searchQuery); // 작품 이름 검색
        }

        return null; // 검색 기준이 없으면 null 반환
    }

    private BooleanExpression searchByArtItemLike(String searchBy, String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return null; // 검색어가 없으면 조건 추가 안 함
        }

        if ("artistName".equalsIgnoreCase(searchBy)) {
            return QArtItem.artItem.artistName.containsIgnoreCase(searchQuery); // 작가 이름 검색
        } else if ("artName".equalsIgnoreCase(searchBy)) {
            return QArtItem.artItem.artName.containsIgnoreCase(searchQuery); // 작품 이름 검색
        }

        return null; // 검색 기준이 없으면 null 반환
    }


    @Override
    public Page<RentalItem> getAdminRentalItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        // QueryDSL을 활용하여 RentalItem 엔티티 검색
        QueryResults<RentalItem> results = queryFactory.selectFrom(QRentalItem.rentalItem)
                .where(
                        searchRentalItemSellStatusEq(itemSearchDto.getSearchSellStatus()), // 판매 상태 조건
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()) // 작가 이름 또는 작품 이름 조건
                )
                .orderBy(QRentalItem.rentalItem.price.asc()) // price 기준 오름차순 정렬
                .offset(pageable.getOffset()) // 페이지 시작점
                .limit(pageable.getPageSize()) // 페이지 크기 제한
                .fetchResults();

        List<RentalItem> content = results.getResults(); // 검색 결과
        long total = results.getTotal(); // 총 결과 개수

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<ArtItem> getAdminArtItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QueryResults<ArtItem> results = queryFactory.selectFrom(QArtItem.artItem)
                .where(
                        searchArtItemSellStatusEq(itemSearchDto.getSearchSellStatus()), // 판매 상태 조건
                        searchByArtItemLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()) // 작가 이름 또는 작품 이름 조건
                )
                .orderBy(QArtItem.artItem.startPrice.asc()) // price 기준 오름차순 정렬
                .offset(pageable.getOffset()) // 페이지 시작점
                .limit(pageable.getPageSize()) // 페이지 크기 제한
                .fetchResults();

        List<ArtItem> content = results.getResults(); // 검색 결과
        long total = results.getTotal(); // 총 결과 개수

        return new PageImpl<>(content, pageable, total);
    }


}
