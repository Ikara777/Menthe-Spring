package com.shop.service;

import com.shop.dto.MyBidDto;
import com.shop.entity.ArtItem;
import com.shop.entity.Bid;
import com.shop.entity.Member;
import com.shop.repository.ArtItemRepository;
import com.shop.repository.BidRepository;
import com.shop.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AuctionService {

    private final ArtItemRepository artItemRepository;
    private final MemberRepository memberRepository;
    private final BidRepository bidRepository;

    public AuctionService(ArtItemRepository artItemRepository, MemberRepository memberRepository, BidRepository bidRepository) {
        this.artItemRepository = artItemRepository;
        this.memberRepository = memberRepository;
        this.bidRepository = bidRepository;
    }

    // 경매 아이템 조회
    public ArtItem getArtItem(Long itemId) {
        return artItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("경매 아이템을 찾을 수 없습니다."));
    }


    public Integer getMaxAmount(Long itemId) {
        Optional<Bid> maxBid = bidRepository.findTopByArtItemIdOrderByMaxAmountDesc(itemId);  // 최대 응찰 금액을 조회
        maxBid.ifPresent(bid -> {
            System.out.println("최대 응찰 금액: " + bid.getBidAmount());
        });
        if (!maxBid.isPresent()) {
            System.out.println("응찰 기록이 없습니다.");
        }


        return maxBid.map(Bid::getMaxAmount).orElse(null);  // 응찰 기록이 없다면 null 반환
    }


    public List<MyBidDto> getMyBids(Long memberId) {
        // 현재 진행 중인 경매 아이템 리스트 가져오기
        List<ArtItem> ongoingArtItems = artItemRepository.findByEventStartBeforeAndEventEndAfter(
                LocalDateTime.now(), LocalDateTime.now());

        // 해당 경매에서 사용자가 응찰한 내역을 필터링
        List<Bid> userBids = bidRepository.findByMemberIdAndArtItemIn(memberId, ongoingArtItems);

        // 같은 artItem에 대한 중복 응찰 내역을 없애기 위해, Map을 사용하여 하나로 그룹화
        Map<Long, MyBidDto> bidMap = new HashMap<>();

        for (Bid bid : userBids) {
            ArtItem artItem = bid.getArtItem();

            // 1. memberId와 artItem에 대해 가장 최근의 응찰 시간 가져오기
            Optional<Bid> highestBidForUser = bidRepository.findTopByMemberIdAndArtItemOrderByBidTimeDesc(memberId, artItem);

            // 2. 최근의 응찰 시간 가져오기
            LocalDateTime highestBidTimeForUser = highestBidForUser.map(Bid::getBidTime).orElse(null);

            // 3. 해당 경매 아이템에 대한 전체 최고 응찰가 (maxAmount가 가장 큰 값)
            Bid highestBid = bidRepository.findTopByArtItemOrderByBidAmountDesc(artItem);
            int highestBidAmount = highestBid != null ? highestBid.getBidAmount() : 0;

            // 4. 내가 응찰한 최고 금액 (사용자가 응찰한 bidAmount 중 가장 높은 값)
            int highestBidAmountForUser = bidRepository.findByMemberIdAndArtItemOrderByBidAmountDesc(memberId, artItem)
                    .stream()
                    .mapToInt(Bid::getBidAmount)
                    .max()
                    .orElse(0);  // 사용자가 응찰한 내역이 없다면 0으로 처리

            // 이미 맵에 해당 artItem이 있으면, 최고 응찰가를 비교하여 갱신
            MyBidDto existingBidDto = bidMap.get(artItem.getId());
            if (existingBidDto == null) {
                // 새로운 artItem에 대한 첫 응찰 내역 추가
                bidMap.put(artItem.getId(), new MyBidDto(
                        artItem.getArtName(),
                        artItem.getArtistName(),
                        artItem.getImgUrl(),
                        highestBidTimeForUser,  // 최근 응찰 시간
                        highestBidAmountForUser,  // 내가 응찰한 최고 금액
                        highestBidAmount  // 전체 최고 응찰가
                ));
            } else {
                // 내가 응찰한 최고 금액이 갱신되었을 때만 BidTime 갱신
                if (existingBidDto.getMaxAmount() < highestBidAmountForUser) {
                    existingBidDto.setMaxAmount(highestBidAmountForUser); // 본인 최고 응찰가 업데이트
                    existingBidDto.setBidTime(highestBidTimeForUser); // 응찰 시간 갱신
                }

                // 전체 최고 응찰가 업데이트 (응찰 시간은 갱신하지 않음)
                if (existingBidDto.getHighestBidAmount() < highestBidAmount) {
                    existingBidDto.setHighestBidAmount(highestBidAmount); // 전체 최고 응찰가 업데이트
                }
            }
        }

        // 중복된 artItem을 제외하고 유니크한 응찰 내역 반환
        return new ArrayList<>(bidMap.values());
    }





}
