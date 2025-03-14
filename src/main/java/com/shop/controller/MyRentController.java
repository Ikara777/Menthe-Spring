package com.shop.controller;

import com.shop.constant.ItemSellStatus;
import com.shop.constant.PaymentStatus;
import com.shop.entity.*;
import com.shop.repository.CashSummaryRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.RentRepository;
import com.shop.repository.RentalItemRepository;
import com.shop.service.RentalItemImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(value = "/myRent")
public class MyRentController {

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CashSummaryRepository cashSummaryRepository;

    @Autowired
    private RentalItemRepository rentalItemRepository;

    @Autowired
    private RentalItemImgService rentalItemImgService;

    // 로그인한 사용자의 대여 목록을 가져오기

    @GetMapping
    public String getMyRentItems(Model model, Principal principal) {
        String username = principal.getName();  // 로그인한 사용자 ID

        // 로그인한 사용자의 정보 가져오기
        Member member = memberRepository.findByUserId(username);

        // 사용자의 대여 목록을 가져오기
        List<Rent> rentItems = rentRepository.findByMember(member);


        // Rent 리스트에서 RentalItem 추출
        List<RentalItem> items = rentItems.stream()
                .map(Rent::getRentalItem) // Rent 객체에서 rentalItem 추출
                .toList(); // 리스트로 변환




        Map<Long, List<RentalItemImg>> itemImagesMap = new HashMap<>();
        for (RentalItem item : items) {
            List<RentalItemImg> images = rentalItemImgService.getImagesByRentalItemId(item.getId());
            itemImagesMap.put(item.getId(), images);
        }



        // 대여 목록과 memberId를 뷰로 전달
        model.addAttribute("itemImagesMap", itemImagesMap);
        model.addAttribute("rentItems", rentItems);
        model.addAttribute("memberId", member.getId());


        return "member/myRent";
    }

    // 환불 처리 후 대여 목록으로 리다이렉트
    @PostMapping("/refund/{rentId}")
    public String processRefund(@PathVariable Long rentId, Principal principal, Model model) {
        Rent rent = rentRepository.findById(rentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rent ID"));

        // 응찰 시간 기준으로 일주일이 경과했는지 확인
        LocalDateTime rentTime = rent.getRentTime();
        LocalDateTime currentTime = LocalDateTime.now();

        if (rentTime.plusWeeks(1).isBefore(currentTime)) {
            // 일주일이 지난 경우, 환불 불가 메시지
            model.addAttribute("errorMessage", "환불 기간이 끝났습니다.");
            return "member/myRent";  // 대여 목록 페이지로 돌아가면서 메시지를 표시
        }

        // 환불 처리: 상태 변경 (PAID -> CANCEL)
        rent.setStatus(PaymentStatus.CANCEL);
        rentRepository.save(rent);

        //Rent 엔티티에서 금액 정보를 가져옴
        int refundAmount = rent.getRentAmount();

        // 로그인한 사용자 정보에서 memberId 추출
        String username = principal.getName();
        Member member = memberRepository.findByUserId(username);

        // 해당 사용자의 CashSummary 찾기
        CashSummary cashSummary = cashSummaryRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("CashSummary not found for member"));

        // 환불된 금액을 totalAmount에 더하기
        cashSummary.setTotalAmount(cashSummary.getTotalAmount() + refundAmount);
        cashSummaryRepository.save(cashSummary);

        // 해당 Rent에 연결된 RentalItem 상태 변경 (SELL로 복원)
        RentalItem rentalItem = rent.getRentalItem();
        rentalItem.setItemSellStatus(ItemSellStatus.SELL);  // 판매 상태를 SELL로 설정
        rentalItemRepository.save(rentalItem);  // RentalItem 저장

        return "redirect:/myRent";
    }
}
