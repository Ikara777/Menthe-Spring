package com.shop.controller;


import com.shop.dto.*;
import com.shop.entity.ArtItem;
import com.shop.entity.ArtItemImg;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import com.shop.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ArtItemController {

    private final ArtItemService artItemService;
    private final MemberService memberService;
    private final AuctionService auctionService;
    private final CashSummaryService cashSummaryService;
    private final MemberRepository memberRepository;
    private final ArtItemImgService artItemImgService;

    @GetMapping(value = "/art")
    public String getAllArtItem(
            ItemSearchDto itemSearchDto,
            @RequestParam(value = "searchBy", required = false, defaultValue = "artName") String searchBy,
            @RequestParam(value = "sort", required = false, defaultValue = "priceAsc") String sort,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            Model model, Principal principal) {

        // 정렬 조건 생성
        Sort sorting;
        if ("priceAsc".equals(sort)) {
            sorting = Sort.by("startPrice").ascending();
        } else if ("priceDesc".equals(sort)) {
            sorting = Sort.by("startPrice").descending();
        } else {
            sorting = Sort.by("id").descending(); // 기본 정렬
        }

        Pageable pageable = PageRequest.of(page, 10, sorting);

        // 검색 조건 처리
        Page<ArtItemDto> artItemDtos = artItemService.getArtItemPage(itemSearchDto, searchBy, pageable);

        String memberId = principal.getName();
        Member member = memberRepository.findByUserId(memberId);


        // 이미지 리스트 추출 추가
        Page<ArtItem> items = artItemService.getAdminArtItemPage(itemSearchDto, pageable);
        Map<Long, List<ArtItemImg>> itemImagesMap = new HashMap<>();
        for (ArtItem item : items) {
            List<ArtItemImg> images = artItemImgService.getImagesByArtItemId(item.getId());
            itemImagesMap.put(item.getId(), images);
        }


        // 모델에 데이터 추가
        model.addAttribute("itemImagesMap", itemImagesMap); // 아이템별 이미지 매핑
        model.addAttribute("member", member.getId());
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("searchBy", searchBy);
        model.addAttribute("sort", sort);
        model.addAttribute("maxPage", 10);
        model.addAttribute("items", artItemDtos);

        return "item/artItem";
    }


    @GetMapping(value = "/art/{artId}")
    public String itemDtl(Model model, @PathVariable("artId") Long artId, Principal principal) {
        // ArtItemDto를 가져오던 기존 로직을 수정하여, ArtItem을 가져옵니다.
        ArtItem artItem = auctionService.getArtItem(artId);
        model.addAttribute("item", artItem);

        // 경매 관련 처리 추가
        String userId = principal.getName();  // 로그인한 사용자 정보
        Member member = memberService.getMemberById(userId);  // 사용자의 정보
        model.addAttribute("member", member.getId());

        Integer maxPrice = auctionService.getMaxAmount(artId);  // 해당 ArtItem에 대한 최대 응찰 금액 조회
        int startPrice = artItem.getStartPrice();  // 시작 가격

        Integer totalSummary = cashSummaryService.getCashSummaryByEmail(member.getEmail()).getTotalAmount();
        System.out.println("****************************");
        System.out.println(totalSummary);
        System.out.println("****************************");

        List<Integer> priceOptions = new ArrayList<>();
        if (maxPrice == null) {
            // maxPrice가 null이면 시작 가격을 기준으로 5개의 가격 옵션 생성
            for (int i = 0; i < 5; i++) {
                priceOptions.add(startPrice + (i * 10000));  // 시작 가격에 10,000씩 더해서 생성
                if (totalSummary < priceOptions.get(i)) {
                    priceOptions.remove(i);
                    break;
                }
            }
        } else {
            maxPrice += 10000;
            // maxPrice가 null이 아니면 maxPrice를 기준으로 5개의 가격 옵션 생성
            for (int i = 0; i < 5; i++) {
                priceOptions.add(maxPrice + (i * 10000));  // maxPrice에 10,000씩 더해서 생성
                if (totalSummary < priceOptions.get(i)) {
                    priceOptions.remove(i);
                    break;
                }
            }
        }

        model.addAttribute("priceOptions", priceOptions);
        System.out.println(artItem.getEventStart() + "eventStart값");
        System.out.println(artItem.getEventEnd() + "eventEnd값");


        // 이미지만 별도로 조회
        List<ArtItemImgDto> itemImgs = artItemService.getArtItemImages(artId);
        model.addAttribute("itemImgs", itemImgs);

        // 출력 확인
        for (ArtItemImgDto img : itemImgs) {
            System.out.println("=======RentalItemId=======: " + artId);
            System.out.println("=======ItemImgId: =======" + img.getId());
            System.out.println("=======ImgName:======= " + img.getImgName());
            System.out.println("=======ImgUrl:======= " + img.getImgUrl());

        }

        // 해당 뷰 페이지 반환
        return "item/artItemDtl";  // 뷰 템플릿 이름을 반환합니다.
    }


    @GetMapping(value = "/not/art")
    public String getAllArtItemNotLogin(
            ItemSearchDto itemSearchDto,
            @RequestParam(value = "searchBy", required = false, defaultValue = "artName") String searchBy,
            @RequestParam(value = "sort", required = false, defaultValue = "priceAsc") String sort,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            Model model) {


        // 정렬 조건 생성
        Sort sorting;
        if ("priceAsc".equals(sort)) {
            sorting = Sort.by("startPrice").ascending();
        } else if ("priceDesc".equals(sort)) {
            sorting = Sort.by("startPrice").descending();
        } else {
            sorting = Sort.by("id").descending(); // 기본 정렬
        }

        Pageable pageable = PageRequest.of(page, 10, sorting);

        // 검색 조건 처리
        Page<ArtItemDto> artItemDtos = artItemService.getArtItemPage(itemSearchDto, searchBy, pageable);


        Page<ArtItem> items = artItemService.getAdminArtItemPage(itemSearchDto, pageable);
        Map<Long, List<ArtItemImg>> itemImagesMap = new HashMap<>();
        for (ArtItem item : items) {
            List<ArtItemImg> images = artItemImgService.getImagesByArtItemId(item.getId());
            itemImagesMap.put(item.getId(), images);
        }


        // 모델에 데이터 추가
        model.addAttribute("itemImagesMap", itemImagesMap); // 아이템별 이미지 매핑
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("searchBy", searchBy);
        model.addAttribute("sort", sort);
        model.addAttribute("maxPage", 10);
        model.addAttribute("items", artItemDtos);

        return "item/artItemNot";
    }


    @GetMapping(value = "/not/art/{artId}")
    public String itemDtlNotLogin(Model model, @PathVariable("artId") Long artId) {

        ArtItem artItem = auctionService.getArtItem(artId);
        model.addAttribute("item", artItem);


        // 이미지만 별도로 조회
        List<ArtItemImgDto> itemImgs = artItemService.getArtItemImages(artId);
        model.addAttribute("itemImgs", itemImgs);


        // 출력 확인
        for (ArtItemImgDto img : itemImgs) {
            System.out.println("=======RentalItemId=======: " + artId);
            System.out.println("=======ItemImgId: =======" + img.getId());
            System.out.println("=======ImgName:======= " + img.getImgName());
            System.out.println("=======ImgUrl:======= " + img.getImgUrl());
        }
        return "item/artItemDtlNot";
    }

}
