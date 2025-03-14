    package com.shop.controller;


    import com.shop.constant.ItemSellStatus;
    import com.shop.dto.ArtItemDto;
    import com.shop.dto.ItemSearchDto;
    import com.shop.entity.ArtItem;
    import com.shop.entity.ArtItemImg;
    import com.shop.service.ArtItemImgService;
    import com.shop.service.ArtItemService;
    import io.micrometer.common.util.StringUtils;
    import jakarta.persistence.EntityNotFoundException;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.validation.BindingResult;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.Optional;

    @Controller // MVC에서의 컨트롤러 역할을 합니다.
    @RequiredArgsConstructor //final 필드나 @NonNull이 붙은 필드에 대해 생성자를 자동으로 만들어줍

    public class ArtItemFormController {

        private final ArtItemService artItemService; // itemService 아이탬 추가를 하는 비즈니스 클래스
        private final ArtItemImgService artItemImgService; // itemService 아이탬 추가를 하는 비즈니스 클래스




        @GetMapping(value = "/admin/artItemForm/new")
        public String itemForm(Model model) {
            model.addAttribute("ItemDto", new ArtItemDto());
            return "item/artItemForm"; // HTML 파일 경로
        }


        @PostMapping(value = "/admin/artItemForm/new")
        public String itemNew(
                @Valid @ModelAttribute("ItemDto") ArtItemDto artItemDto,
                BindingResult bindingResult,
                Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {

            if (bindingResult.hasErrors()) {
                return "item/artItemForm"; // 에러 발생 시 동일한 폼으로 돌아감
            }

            if(itemImgFileList.get(0).isEmpty()  && artItemDto.getImgUrl().trim().isEmpty()){
                model.addAttribute("errorMessage",
                        "첫번째 상품 이미지 또는 메인 이미지 `URL`은 필수 입력 값입니다.");
                return "item/artItemForm";
            }


            try {
                artItemService.saveArtItem(artItemDto, itemImgFileList);
            } catch (Exception e) {
                model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
                return "item/artItemForm";
            }

            return "redirect:/";
        }


        @GetMapping(value = {"/admin/artItems", "/admin/artItems/{page}"})
        public String itemManage(@RequestParam(value = "searchBy", required = false) String searchBy,
                                 @RequestParam(value = "searchQuery", required = false) String searchQuery,
                                 @RequestParam(value = "searchSellStatus", required = false) String searchSellStatus,
                                 @PathVariable("page") Optional<Integer> page,
                                 Model model) {

            Pageable pageable = PageRequest.of(page.orElse(0), 5);
            ItemSearchDto itemSearchDto = new ItemSearchDto();

            // 검색 조건 설정
            if (!StringUtils.isEmpty(searchBy) && !StringUtils.isEmpty(searchQuery)) {
                itemSearchDto.setSearchBy(searchBy);
                itemSearchDto.setSearchQuery(searchQuery);
            }

            // 판매 상태 처리
            if (!StringUtils.isEmpty(searchSellStatus)) {
                if (!"ALL".equalsIgnoreCase(searchSellStatus)) {
                    try {
                        ItemSellStatus sellStatus = ItemSellStatus.valueOf(searchSellStatus);
                        itemSearchDto.setSearchSellStatus(sellStatus);
                    } catch (IllegalArgumentException e) {
                        model.addAttribute("errorMessage", "유효하지 않은 판매 상태입니다.");
                        return "redirect:/"; // 유효하지 않은 값으로 메인 페이지 리다이렉트
                    }
                }
            }
            Page<ArtItem> items = artItemService.getAdminArtItemPage(itemSearchDto, pageable);

            Map<Long, List<ArtItemImg>> itemImagesMap = new HashMap<>();
            for (ArtItem item : items) {
                List<ArtItemImg> images = artItemImgService.getImagesByArtItemId(item.getId());
                itemImagesMap.put(item.getId(), images);
            }

            // 서비스 호출 및 결과 반환

            model.addAttribute("items", items);
            model.addAttribute("itemImagesMap", itemImagesMap); // 아이템별 이미지 매핑
            model.addAttribute("itemSearchDto", itemSearchDto);
            model.addAttribute("maxPage", 5);

            return "item/artItemMng";
        }


        @GetMapping(value = "/admin/artItemForm/{itemId}") //아이템의 정보를 읽는 메서드
        public String itemDtl(@PathVariable("itemId")Long itemId, Model model){


            try {
                ArtItemDto itemFormDto = artItemService.getArtItemDtl(itemId);
                System.out.println("==============아이템 가격 볌위"+itemFormDto.getPriceRange());
                model.addAttribute("ItemDto", itemFormDto);

            }catch (EntityNotFoundException e){
                model.addAttribute("errorMessage","존재하지 않는 상품입니다.");
                model.addAttribute("ItemDto",new ArtItemDto());
                return "item/artItemForm";
            }
            return "item/artItemForm";
        }


        @PostMapping(value = "/admin/artItemForm/{itemId}")
        public String itemUpdate(@Valid @ModelAttribute("ItemDto") ArtItemDto itemFormDto,
                                 BindingResult bindingResult,
                                 Model model,
                                 @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {
            System.out.println("======= 아이템 메니저 추출 아이템 상세(Dtl) 목록 =======");

            System.out.println("==============아이템 가격 볌위"+itemFormDto.getPriceRange());

            // 유효성 검사 에러 처리
            if (bindingResult.hasErrors()) {
                model.addAttribute("ItemDto", itemFormDto); // 에러 발생 시 데이터를 유지
                return "item/artItemForm";
            }

            if(itemImgFileList.get(0).isEmpty()  && itemFormDto.getImgUrl().trim().isEmpty()){
                model.addAttribute("errorMessage",
                        "첫번째 상품 이미지 또는 메인 이미지 `URL`은 필수 입력 값입니다.");
                return "item/artItemForm";
            }


            try {
                artItemService.updateArtItem(itemFormDto, itemImgFileList);
            } catch (Exception e) {
                model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
                model.addAttribute("ItemDto", itemFormDto); // 에러 발생 시 데이터를 유지
                return "item/artItemForm";
            }

            return "redirect:/"; // 수정 후 메인 페이지로 리다이렉트
        }


        @PostMapping(value = "/admin/deleteArtItem/{itemId}")
        public String deleteArtItem(@PathVariable("itemId") Long itemId, Model model) {
            try {
                // 아이템 및 관련 이미지 삭제
                artItemService.deleteArtItem(itemId);
            } catch (Exception e) {
                // 삭제 중 에러 발생 시 메시지와 함께 폼으로 돌아감
                model.addAttribute("errorMessage", "상품 삭제 중 에러가 발생하였습니다.");
                return "item/artItemForm";
            }

            // 삭제 완료 후 메인 페이지로 리다이렉트
            return "redirect:/";
        }



    }

