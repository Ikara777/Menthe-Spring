package com.shop.controller;

import com.shop.dto.ArtistDto;
import com.shop.dto.RentalDto;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import com.shop.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final RankingService rankingService;
    private final MemberRepository memberRepository;
    private final PaymentService paymentService;
    private final RentService rentService;
    private final CashSummaryService cashSummaryService;
    private final BidService bidService;
    private final MemberService memberService;


    @GetMapping(value = "/admin/adminPage")
    public String adminPage() {

        return "member/adminPage";
    }

    @GetMapping(value = "/")
    public String main(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model, Principal principal) {
        // 랭킹 데이터
        List<ArtistDto> artistRankings = rankingService.getArtistRankings();
        artistRankings = artistRankings.stream().limit(5).collect(Collectors.toList());

        List<RentalDto> rentalRankings = rankingService.getRentalRankings();
        rentalRankings = rentalRankings.stream().limit(5).collect(Collectors.toList());




        if (principal == null) {
            String memberRole = "NONE";
            model.addAttribute("memberRole", memberRole);
        }


        // layout1 과의 충돌을 막기 위한 NONE 값 주입
        if (principal != null) {
            Member member = memberService.getMemberByNullTest(principal.getName());
            if (member == null) {
                String memberRole = "NONE";
                model.addAttribute("memberRole", memberRole);
                // 데이터 모델에 추가
                model.addAttribute("artistRankings", artistRankings);
                model.addAttribute("rentalRankings", rentalRankings);
                model.addAttribute("itemSearchDto", itemSearchDto);
                // 메인 페이지 반환
                return "main";
            }
        }


        if (principal != null) {
            String memberId = principal.getName();
            Member member = memberRepository.findByUserId(memberId);
            String memberRole = String.valueOf(member.getRole());
            model.addAttribute("memberRole", memberRole);
            model.addAttribute("member", member.getId());



            //-------- 프린시펄 및 케시 서머리 시험

            String email = member.getEmail();
            String name = member.getName(); // 이름도 가져온다고 가정 (수정 필요)


            model.addAttribute("memberId", memberId);

            // Payment 테이블에서 merchant_uid가 'Cache-plus'인 amount 합산
            Integer paymentAmount = paymentService.getTotalAmountByEmailAndMerchantUid(email, "Cache-plus");
            Integer rentAmount = rentService.getTotalAmountByMemberId(member.getId());
            Integer purchaseAmount = bidService.getTotalBidAmountByMemberId(member.getId());
            Integer totalAmount = paymentAmount - rentAmount - purchaseAmount;

            System.out.println("---토탈 어먼트----"+paymentAmount);
            System.out.println("---토탈 어먼트----"+totalAmount);


            // 계산된 잔액을 CashSummary 테이블에 저장 또는 업데이트
            cashSummaryService.saveOrUpdateCashSummary(email, name, totalAmount);
        }

        // 데이터 모델에 추가
        model.addAttribute("artistRankings", artistRankings);
        model.addAttribute("rentalRankings", rentalRankings);
        model.addAttribute("itemSearchDto", itemSearchDto);

        // 메인 페이지 반환
        return "main";
    }
}
