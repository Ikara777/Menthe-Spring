package com.shop.controller;


import com.shop.dto.BidDto;
import com.shop.dto.MyBidDto;
import com.shop.entity.*;
import com.shop.repository.ArtItemRepository;
import com.shop.service.BidService;
import com.shop.service.AuctionService;
import com.shop.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AuctionController {

    private final AuctionService auctionService;
    private final BidService bidService;
    private final MemberService memberService;
    private final ArtItemRepository artItemRepository;

    public AuctionController(AuctionService auctionService, BidService bidService, MemberService memberService, ArtItemRepository artItemRepository) {
        this.auctionService = auctionService;
        this.bidService = bidService;
        this.memberService = memberService;
        this.artItemRepository = artItemRepository;
    }

    // 경매 응찰하기
    @PostMapping("/auction/art")
    public String submitBid(@ModelAttribute BidDto bidDto, Principal principal) {
        // Principal에서 userId 가져오기
        String userId = principal.getName(); // 로그인한 사용자의 userId

        // MemberService를 통해 Member 조회
        Member member = memberService.getMemberById(userId);

        // 경매 아이템 조회 (BidDto에서 itemId를 가져와서 사용) -- 이상없으면 지우기***
        ArtItem artItem = auctionService.getArtItem(bidDto.getItemId());

        // 응찰 처리
        bidService.submitBid(bidDto, member);


        return "redirect:/art/" + bidDto.getItemId();
    }



    @GetMapping("/bid/my-bids/{memberId}")
    public String getMyBids(@PathVariable Long memberId, Model model) {
        // 최근 5개의 응찰 내역만 가져오도록 처리
        List<MyBidDto> myBids = auctionService.getMyBids(memberId);

        // 응찰 시간 기준으로 내림차순 정렬 (가장 최근 응찰이 첫 번째로 오도록)
        List<MyBidDto> sortedBids = myBids.stream()
                .sorted((bid1, bid2) -> bid2.getBidTime().compareTo(bid1.getBidTime()))  // 내림차순 정렬
                .collect(Collectors.toList());

        // 최근 5개만 추출
        List<MyBidDto> recentBids = sortedBids.stream()
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("bids", recentBids);
        return "member/myBid";
    }



}
