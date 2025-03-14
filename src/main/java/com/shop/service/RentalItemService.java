package com.shop.service;


import com.shop.constant.ItemSellStatus;
import com.shop.dto.*;
import com.shop.entity.*;
import com.shop.repository.ItemRepositoryCustom;
import com.shop.repository.RentalItemImgRepository;
import com.shop.repository.RentalItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Qualifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalItemService {


    private final RentalItemRepository rentalItemRepository;
    private final RentalItemImgService rentalItemImgService;
    private final RentalItemImgRepository rentalItemImgRepository;

    public Long saveRentalItem(RentalItemDto rentalItemDto, List<MultipartFile> itemImgFileList)
            throws Exception{
        //상품등록
        RentalItem rentalItem = rentalItemDto.createRentalItem();
        rentalItemRepository.save(rentalItem);



        for(int i =0;i<itemImgFileList.size();i++){ // 5번 반복
            RentalItemImg rentalItemImg = new RentalItemImg();
            rentalItemImg.setRentalItem(rentalItem);
            if(i==0)
                rentalItemImg.setRepImgYn("Y"); // 0
            else
                rentalItemImg.setRepImgYn("N"); // 1 2 3 4
            rentalItemImgService.saveItemImg(rentalItemImg,itemImgFileList.get(i));
        }




        //이미지 등록
        return rentalItem.getId();
    }



    @Transactional(readOnly = true)
    public Page<RentalItemDto> getRentalItemPage(ItemSearchDto itemSearchDto, String searchBy, Pageable pageable) {
        // 검색 조건에 따른 동적 처리
        Page<RentalItem> rentalItems;
        if ("artistName".equals(searchBy)) {
            rentalItems = rentalItemRepository.findByArtistNameContaining(itemSearchDto.getSearchQuery(), pageable);
        } else {
            rentalItems = rentalItemRepository.findByArtNameContaining(itemSearchDto.getSearchQuery(), pageable);
        }

        // ArtItem -> ArtItemDto 변환
        return rentalItems.map(RentalItemDto::of);
    }


    @Transactional(readOnly = true)
    public RentalItemDto getRentalItemById(Long artId) {
        System.out.println("getRentalItemById() 메서드 호출됨");
        System.out.println("요청된 Art ID: " + artId);
        return rentalItemRepository.findById(artId)
                .map(RentalItemDto::of)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 작품입니다. ID: " + artId));
    }


    public RentalItem updateRentalItemStatusToSOLD_OUT(Long ItemId) {


            // impUid로 Payment 검색
            RentalItem rentalItem = rentalItemRepository.findAllById(ItemId);

            // 결제 상태를 CANCEL로 변경
            rentalItem.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            System.out.println("변경 후 상태: " + rentalItem.getItemSellStatus());

            // 업데이트
            rentalItemRepository.save(rentalItem);
            System.out.println("결제 상태가 CANCEL로 저장되었습니다.");


        return rentalItem;
    }


    @Transactional(readOnly = true) // 읽기 전용 더티체킹(변경감지)
    public RentalItemDto getRentalItemDtl(Long itemId){
        //Entity
        List<RentalItemImg> itemImgList = rentalItemImgRepository.findByRentalItemIdOrderByIdAsc(itemId);
        //DB에서 데이터를 가지고 옵니다.
        //DTO
        List<RentalItemImgDto> itemImgDtoList = new ArrayList<>(); //왜 DTO 만들었나요??

        for(RentalItemImg itemimg : itemImgList){
            // Entity -> DTO
            RentalItemImgDto itemImgDto = RentalItemImgDto.of(itemimg);
            itemImgDtoList.add(itemImgDto);
        }

        RentalItem item = rentalItemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        // Item -> ItemFormDto modelMapper
        RentalItemDto itemFormDto = RentalItemDto.of(item);
        itemFormDto.setRentalItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }


    @Transactional
    public void updateRentalItem(RentalItemDto rentalItemDto, List<MultipartFile> itemImgFileList) throws Exception {
        // 1. 기존 렌탈 아이템 로드
        RentalItem rentalItem = rentalItemRepository.findById(rentalItemDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다."));
        rentalItem.updateRentalItem(rentalItemDto);

        // 2. 기존 이미지 목록 로드
        List<RentalItemImg> rentalItemImgList = rentalItemImgRepository.findByRentalItemIdOrderByIdAsc(rentalItem.getId());

        // 3. 이미지 테이블 생성 및 업데이트 로직
        for (int i = 0; i < itemImgFileList.size(); i++) {
            MultipartFile itemImgFile = itemImgFileList.get(i);

            if (i < rentalItemImgList.size()) {
                // 기존 이미지가 있으면 업데이트
                rentalItemImgService.updateItemImg(rentalItemImgList.get(i).getId(), itemImgFile);
            } else {
                // 기존 이미지가 없는 경우 새로 생성
                RentalItemImg newRentalItemImg = new RentalItemImg();
                newRentalItemImg.setRentalItem(rentalItem);

                if (!itemImgFile.isEmpty()) {
                    // 이미지가 있다면 저장
                    rentalItemImgService.saveItemImg(newRentalItemImg, itemImgFile);
                } else {
                    // 이미지가 없으면 테이블만 생성
                    newRentalItemImg.setOriImgName(null);
                    newRentalItemImg.setImgName(null);
                    newRentalItemImg.setImgUrl(null);
                    rentalItemImgRepository.save(newRentalItemImg);
                }
            }
        }
    }


    @Transactional
    public void deleteRentalItem(Long rentalItemId) throws Exception {
        // 1. 삭제할 아트 아이템 로드
        RentalItem rentalItem = rentalItemRepository.findById(rentalItemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다."));

        // 2. 관련 이미지 로드
        List<RentalItemImg> rentalItemImgList = rentalItemRepository.findImagesByRentalItemId(rentalItemId);

        // 3. 이미지 삭제 로직
        for (RentalItemImg rentalItemImg : rentalItemImgList) {
            // 이미지 삭제 서비스 호출 (파일 삭제와 DB 기록 삭제)
            rentalItemImgService.deleteItemImg(rentalItemImg.getId());
        }

        // 4. 아트 아이템 삭제
        rentalItemRepository.delete(rentalItem);

        System.out.println("아이템 및 관련 이미지가 삭제되었습니다: ID = " + rentalItemId);
    }


    @Transactional(readOnly = true) // 이미지 리스트를 Page로 가져오는 서비스문
    public Page<RentalItem> getAdminRentalItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return rentalItemRepository.getAdminRentalItemPage(itemSearchDto,pageable);
    }






    public List<RentalItemImgDto> getRentalItemImages(Long rentalItemId) {
        // ItemImg 데이터 조회
        List<RentalItemImg> rentalItemImgs = rentalItemImgRepository.findByRentalItemIdOrderByIdAsc(rentalItemId);

        // ItemImgDto 리스트로 변환하여 반환
        return rentalItemImgs.stream()
                .map(RentalItemImgDto::of)
                .toList();
    }

}
