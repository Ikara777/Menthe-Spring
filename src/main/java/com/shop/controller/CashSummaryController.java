package com.shop.controller;

import com.shop.entity.Member;
import com.shop.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/cash-summary") // API 요청 경로로 설정
public class CashSummaryController {

    private final PaymentService paymentService;
    private final MemberService memberService;
    private final RentService rentService;
    private final CashSummaryService cashSummaryService;
    private final BidService bidService;


    public CashSummaryController(PaymentService paymentService, MemberService memberService, RentService rentService,
                                 CashSummaryService cashSummaryService, BidService bidService) {
        this.paymentService = paymentService;
        this.memberService = memberService;
        this.rentService = rentService;
        this.cashSummaryService = cashSummaryService;
        this.bidService = bidService;
    }


    // 로그인된 사용자의 캐시 잔액 계산
    @GetMapping("/balance")
    public String getCashBalance(@AuthenticationPrincipal UserDetails userDetails, Principal principal, Model model) {
        if (userDetails != null) {
            // 로그인된 사용자의 이메일과 이름 가져오기
            String userId = principal.getName(); // 로그인한 사용자 ID (예: ads1313)

            Member member = memberService.getMemberById(userId); // MemberService에서 사용자 정보 조회

            Long memberId = member.getId();
            model.addAttribute("memberId", memberId);


            String email = member.getEmail();
            String name = member.getName(); // 이름도 가져온다고 가정 (수정 필요)

            System.out.println("사용자 이메일: " + email);
            System.out.println("사용자 이름: " + name);

            // Payment 테이블에서 merchant_uid가 'Cache-plus'인 amount 합산
            Integer paymentAmount = paymentService.getTotalAmountByEmailAndMerchantUid(email, "Cache-plus");
            Integer rentAmount = rentService.getTotalAmountByMemberId(member.getId());
            Integer purchaseAmount = bidService.getTotalBidAmountByMemberId(member.getId());
            Integer totalAmount = paymentAmount - rentAmount - purchaseAmount;

            System.out.println("---토탈 어먼트----"+paymentAmount);
            System.out.println("---토탈 어먼트----"+totalAmount);


            // 계산된 잔액을 CashSummary 테이블에 저장 또는 업데이트
            cashSummaryService.saveOrUpdateCashSummary(email, name, totalAmount);


            model.addAttribute("totalAmount", totalAmount);


            return "member/cash";
        }

        // 비로그인 사용자는 0을 전달
        model.addAttribute("totalAmount", 0);
        return "member/cash";
    }
}
