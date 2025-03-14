    package com.shop.controller;


    import com.shop.constant.ItemSellStatus;
    import com.shop.dto.ItemSearchDto;
    import com.shop.dto.RentalItemDto;
    import com.shop.entity.ArtItem;
    import com.shop.entity.ArtItemImg;
    import com.shop.entity.RentalItem;
    import com.shop.entity.RentalItemImg;
    import com.shop.service.RentalItemImgService;
    import com.shop.service.RentalItemService;
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
    @RequiredArgsConstructor //final 필드나 @NonNull이 붙은 필드에 대해 생성자를 자동으로 만듦

    public class RentalItemFormController {

        private final RentalItemService rentalItemService; // itemService 아이탬 추가를 하는 비즈니스 클래스
        private final RentalItemImgService rentalItemImgService;

        @GetMapping(value = "/admin/rentalItemForm/new")
        public String itemForm(Model model) {

            model.addAttribute("ItemDto", new RentalItemDto());


            return "item/rentalItemForm"; // HTML 파일 경로
        }


        @PostMapping(value = "/admin/rentalItemForm/new") // 새로운 아이탬을 추가하기 위한 HTML 폼
        public String itemNew(@Valid @ModelAttribute("ItemDto") RentalItemDto rentalItemDto,
                              BindingResult bindingResult,
                              Model model,
                              @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList){

            // HTML 폼에서 전송된 itemImgFile 데이터를 매개변수로 받을 때 사용됩니다.
            if(bindingResult.hasErrors()){
                return "item/rentalItemForm"; //에러가 나게 되면 "item/itemForm" 을 반환하여 다시 입력하게 함
            }


            if(itemImgFileList.get(0).isEmpty()  && rentalItemDto.getImgUrl().trim().isEmpty()){
                model.addAttribute("errorMessage",
                        "첫번째 상품 이미지 또는 메인 이미지 `URL`은 필수 입력 값입니다.");
                return "item/rentalItemForm";
            }


            try {
                rentalItemService.saveRentalItem(rentalItemDto, itemImgFileList);
                // 성공하면 itemService에 아이탬을 담는다.

            }catch (Exception e){

                model.addAttribute("errorMessage",
                        "상품 등록 중 에러가 발생하였습니다.");
                return "item/rentalItemForm";

            }

            return "redirect:/";
            // 상품을 담으면 메인으로 돌아옴

        }

        @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
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

            Page<RentalItem> items = rentalItemService.getAdminRentalItemPage(itemSearchDto, pageable);

            Map<Long, List<RentalItemImg>> itemImagesMap = new HashMap<>();
            for (RentalItem item : items) {
                List<RentalItemImg> images = rentalItemImgService.getImagesByRentalItemId(item.getId());
                itemImagesMap.put(item.getId(), images);
            }

            // 서비스 호출 및 결과 반환

            model.addAttribute("items", items);
            model.addAttribute("itemImagesMap", itemImagesMap); // 아이템별 이미지 매핑
            model.addAttribute("itemSearchDto", itemSearchDto);
            model.addAttribute("maxPage", 5);

            return "item/rentalItemMng";
        }


        @GetMapping(value = "/admin/rentalItemForm/{itemId}") //아이템의 정보를 읽는 메서드
        public String itemDtl(@PathVariable("itemId")Long itemId, Model model){
            System.out.println("======= 아이템 메니저 추출 (exexexexe) =======");
            System.out.println("======= 아이템 메니저 추출 (exexexexe) =======");
            System.out.println("======= 아이템 메니저 추출 (exexexexe) =======");

            System.out.println(" --아이템 itemDtl-- ");

            try {
                RentalItemDto itemFormDto = rentalItemService.getRentalItemDtl(itemId);
                model.addAttribute("ItemDto", itemFormDto);
            }catch (EntityNotFoundException e){
                model.addAttribute("errorMessage","존재하지 않는 상품입니다.");
                model.addAttribute("ItemDto",new RentalItemDto());
                return "item/rentalItemForm";
            }
            return "item/rentalItemForm";
        }


        @PostMapping(value = "/admin/rentalItemForm/{itemId}")
        public String itemUpdate(@Valid @ModelAttribute("ItemDto") RentalItemDto itemFormDto,
                                 BindingResult bindingResult,
                                 Model model,
                                 @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList
                                 ) {
            System.out.println("======= 아이템 메니저 추출 아이템 상세(Dtl) 목록 =======");

            // 유효성 검사 에러 처리
            if (bindingResult.hasErrors()) {
                model.addAttribute("ItemDto", itemFormDto); // 에러 발생 시 데이터를 유지
                return "item/rentalItemForm";
            }

            // 첫 번째 이미지 검증

            if(itemImgFileList.get(0).isEmpty()  && itemFormDto.getImgUrl().trim().isEmpty()){
                model.addAttribute("errorMessage",
                        "첫번째 상품 이미지 또는 메인 이미지 `URL`은 필수 입력 값입니다.");
                return "item/rentalItemForm";
            }

            try {
                rentalItemService.updateRentalItem(itemFormDto, itemImgFileList);
            } catch (Exception e) {
                model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
                model.addAttribute("ItemDto", itemFormDto); // 에러 발생 시 데이터를 유지
                return "item/rentalItemForm";
            }

            return "redirect:/"; // 수정 후 메인 페이지로 리다이렉트
        }

        @PostMapping(value = "/admin/deleteRentalItem/{itemId}")
        public String deleteRentalItem(@PathVariable("itemId") Long itemId, Model model) {
            try {
                // 아이템 및 관련 이미지 삭제
                rentalItemService.deleteRentalItem(itemId);
            } catch (Exception e) {
                // 삭제 중 에러 발생 시 메시지와 함께 폼으로 돌아감
                model.addAttribute("errorMessage", "상품 삭제 중 에러가 발생하였습니다.");
                return "item/rentalItemForm";
            }

            // 삭제 완료 후 메인 페이지로 리다이렉트
            return "redirect:/";
        }


    }

