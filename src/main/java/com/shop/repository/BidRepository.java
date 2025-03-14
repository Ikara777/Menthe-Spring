package com.shop.repository;

import com.shop.entity.ArtItem;
import com.shop.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {


    @Query("SELECT MAX(b.bidAmount) FROM Bid b WHERE b.artItem.id = :artItemId")
    Integer findMaxAmountByArtId(@Param("artItemId")Long artItemId);


    Optional<Bid> findTopByArtItemIdOrderByMaxAmountDesc(Long artItemId);

    List<Bid> findByArtItem(ArtItem artItem);

    List<Bid> findByMemberIdAndArtItemIn(Long memberId, List<ArtItem> artItems);

    // 해당 경매 아이템에서 최고 응찰가를 가져오는 쿼리
    Bid findTopByArtItemOrderByBidAmountDesc(ArtItem artItem);

    // 특정 사용자가 응찰한 금액 중 가장 높은 금액을 가져오는 쿼리
    List<Bid> findByMemberIdAndArtItemOrderByBidAmountDesc(Long memberId, ArtItem artItem);

    Optional<Bid> findTopByMemberIdAndArtItemOrderByBidTimeDesc(Long memberId, ArtItem artItem);

    @Query("SELECT SUM(p.bidAmount) FROM Purchase p WHERE p.member.id = :memberId AND p.paymentStatus = com.shop.constant.PaymentStatus.PAID")
    Optional<Integer> sumBidAmountByMemberId(@Param("memberId") Long memberId);


}