package com.shop.controller;

import com.shop.entity.Bid;
import com.shop.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@Controller
public class SuccessfulBidController {

    @Autowired
    private BidService bidService;


    @GetMapping("/bid/success/{memberId}")
    public String getMyBids(@PathVariable Long memberId, Model model) {
        // 사용자가 낙찰받은 경매 내역만 가져오기
        List<Bid> successfulBids = bidService.getSuccessfulBidsByMember(memberId);


        // 모델에 데이터를 담아 뷰로 전달
        model.addAttribute("bids", successfulBids);
        return "member/successfulBid";
    }

    @PostMapping("/bid/confirm-purchase")
    public ResponseEntity<?> confirmPurchase(@RequestParam Long memberId, @RequestParam Long bidId) {
        try {
            // 구매 확정 로직
            bidService.confirmPurchase(memberId, bidId);
            return ResponseEntity.ok().build();  // 성공
        } catch (IllegalArgumentException e) {
            // 잔액 부족 오류 처리
            if (e.getMessage().contains("잔액이 부족합니다.")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잔액이 부족합니다.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예상치 못한 오류가 발생했습니다.");
        }
    }

}
