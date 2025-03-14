package com.shop.controller;

import com.shop.dto.*;
import com.shop.entity.*;
import com.shop.repository.BidRepository;
import com.shop.repository.MemberRepository;
import com.shop.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class RentalItemController {

    private final RentalItemService rentalItemService;
    private final MemberService memberService;
    private final RentService rentService;
    private final CashSummaryService cashSummaryService;
    private final PaymentService paymentService;
    private final BidRepository bidRepository;
    private final RentalItemImgService rentalItemImgService;
    private final MemberRepository memberRepository;

    @GetMapping(value = "/rental")
    public String getAllRentalItem(
            ItemSearchDto itemSearchDto,
            @RequestParam(value = "searchBy", required = false, defaultValue = "artName") String searchBy,
            @RequestParam(value = "sort", required = false, defaultValue = "priceAsc") String sort,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            Principal principal,
            Model model) {

        // 정렬 조건 생성
        Sort sorting;
        if ("priceAsc".equals(sort)) {
            sorting = Sort.by("price").ascending(); // 수정: startPrice -> price
        } else if ("priceDesc".equals(sort)) {
            sorting = Sort.by("price").descending(); // 수정: startPrice -> price
        } else {
            sorting = Sort.by("id").descending(); // 기본 정렬
        }

        Pageable pageable = PageRequest.of(page, 10, sorting);

        // 검색 조건 처리
        Page<RentalItemDto> rentalItemDtos = rentalItemService.getRentalItemPage(itemSearchDto, searchBy, pageable);

        if (principal != null) {
            String memberId = principal.getName();
            Member member = memberRepository.findByUserId(memberId);
            model.addAttribute("member", member.getId());
        }

        // 이미지 리스트 추출 추가
        Page<RentalItem> items = rentalItemService.getAdminRentalItemPage(itemSearchDto, pageable);
        Map<Long, List<RentalItemImg>> itemImagesMap = new HashMap<>();
        for (RentalItem item : items) {
            List<RentalItemImg> images = rentalItemImgService.getImagesByRentalItemId(item.getId());
            itemImagesMap.put(item.getId(), images);
        }

        // 모델에 데이터 추가
        model.addAttribute("itemImagesMap", itemImagesMap); // 아이템별 이미지 매핑

        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("searchBy", searchBy);
        model.addAttribute("sort", sort);
        model.addAttribute("maxPage", 10);
        model.addAttribute("items", rentalItemDtos);

        return "item/rentalItem";
    }



    @GetMapping(value = "/rental/{artId}")
    public String itemDtl(Model model, @PathVariable("artId") Long artId,Principal principal) {

        if (principal != null) {
            String userId = principal.getName();
            Member member = memberService.getMemberById(userId);
            String email = member.getEmail();
            String name = member.getName();
            Integer paymentAmount = paymentService.getTotalAmountByEmailAndMerchantUid(email, "Cache-plus");
            Integer rentAmount = rentService.getTotalAmountByMemberId(member.getId());
            Integer totalAmount = paymentAmount - rentAmount;
            cashSummaryService.saveOrUpdateCashSummary(email, name, totalAmount);

            model.addAttribute("member", member.getId());

            if (totalAmount == null) {
                totalAmount = 0 ; // 기본값 설정
            }
            model.addAttribute("cashSummary", totalAmount);
            model.addAttribute("memberCash", member);
        } else {

            model.addAttribute("member", null);
            model.addAttribute("cashSummary", 0); // 비로그인 사용자는 기본값 0

        }


        RentalItemDto rentalItemDto = rentalItemService.getRentalItemById(artId);
        model.addAttribute("item", rentalItemDto);

        // 이미지만 별도로 조회
        java.util.List<RentalItemImgDto> itemImgs = rentalItemService.getRentalItemImages(artId);
        model.addAttribute("itemImgs", itemImgs);

        // 출력 확인
        for (RentalItemImgDto img : itemImgs) {
            System.out.println("=======RentalItemId=======: " + artId);
            System.out.println("=======ItemImgId: =======" + img.getId());
            System.out.println("=======ImgName:======= " + img.getImgName());
            System.out.println("=======ImgUrl:======= " + img.getImgUrl());

        }

        return "item/rentalItemDtl";
    }



    @PostMapping("/rental/rent")
    public String submitBid(@ModelAttribute RentDto rentDto, Principal principal) {
        // Principal에서 userId 가져오기
        String userId = principal.getName(); // 로그인한 사용자의 userId

        // MemberService를 통해 Member 조회
        Member member = memberService.getMemberById(userId);

        // 응찰 처리
        rentService.submitRent(rentDto, member);
        rentalItemService.getRentalItemById(rentDto.getItemId());
        System.out.println("====겟 아이탬 아이디===="+rentDto.getItemId());
        rentalItemService.updateRentalItemStatusToSOLD_OUT(rentDto.getItemId());



        // 구매 즉시 totalAmount의 값을 수정할 수 있게 반영
        String email = member.getEmail();
        String name = member.getName();

        Integer paymentAmount = paymentService.getTotalAmountByEmailAndMerchantUid(email, "Cache-plus");
        Integer rentAmount = rentService.getTotalAmountByMemberId(member.getId());
        Integer purchaseAmount = bidRepository.sumBidAmountByMemberId(member.getId()).orElse(0);

        Integer totalAmount = paymentAmount - rentAmount - purchaseAmount;
        cashSummaryService.saveOrUpdateCashSummary(email, name, totalAmount);


        return "redirect:/";
    }

}
