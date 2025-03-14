package com.shop.service;


import com.shop.dto.*;
import com.shop.entity.ArtItem;
import com.shop.entity.ArtItemImg;

import com.shop.repository.ArtItemImgRepository;
import com.shop.repository.ArtItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class    ArtItemService {

    private final ArtItemRepository artItemRepository;
    private final ArtItemImgService artItemImgService;
    private final ArtItemImgRepository artItemImgRepository;

    public Long saveArtItem(ArtItemDto artItemDto, List<MultipartFile> itemImgFileList)
            throws Exception{
        //상품등록
        ArtItem artItem = artItemDto.createArtItem();
        artItemRepository.save(artItem);

        for(int i =0;i<itemImgFileList.size();i++){ // 5번 반복
            ArtItemImg artItemImg = new ArtItemImg();
            artItemImg.setArtItem(artItem);
            if(i==0)
                artItemImg.setRepImgYn("Y"); // 0
            else
                artItemImg.setRepImgYn("N"); // 1 2 3 4
            artItemImgService.saveItemImg(artItemImg,itemImgFileList.get(i));
        }


        //이미지 등록
        return artItem.getId();
    }


    @Transactional(readOnly = true)
    public Page<ArtItemDto> getArtItemPage(ItemSearchDto itemSearchDto, String searchBy, Pageable pageable) {
        // 검색 조건에 따른 동적 처리
        Page<ArtItem> artItems;
        if ("artistName".equals(searchBy)) {
            artItems = artItemRepository.findByArtistNameContaining(itemSearchDto.getSearchQuery(), pageable);
        } else {
            artItems = artItemRepository.findByArtNameContaining(itemSearchDto.getSearchQuery(), pageable);
        }

        // ArtItem -> ArtItemDto 변환
        return artItems.map(ArtItemDto::of);
    }


    @Transactional(readOnly = true) // 읽기 전용 더티체킹(변경감지)
    public ArtItemDto getArtItemDtl(Long itemId){
        //Entity
        List<ArtItemImg> itemImgList = artItemImgRepository.findByArtItemIdOrderByIdAsc(itemId);
        //DB에서 데이터를 가지고 옵니다.
        //DTO
        List<ArtItemImgDto> itemImgDtoList = new ArrayList<>(); //왜 DTO 만들었나요??

        for(ArtItemImg itemimg : itemImgList){
            // Entity -> DTO
            ArtItemImgDto itemImgDto = ArtItemImgDto.of(itemimg);
            itemImgDtoList.add(itemImgDto);
        }

        ArtItem item = artItemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        // Item -> ItemFormDto modelMapper
        ArtItemDto itemFormDto = ArtItemDto.of(item);
        System.out.println("==============서비스 - 아이템 가격 볌위"+itemFormDto.getPriceRange());
        itemFormDto.setArtItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }



    @Transactional
    public void updateArtItem(ArtItemDto artItemDto, List<MultipartFile> itemImgFileList) throws Exception {
        // 1. 기존 렌탈 아이템 로드
        ArtItem artItem = artItemRepository.findById(artItemDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다."));
        artItem.updateArtItem(artItemDto);

        // 2. 기존 이미지 목록 로드
        List<ArtItemImg> artItemImgList = artItemImgRepository.findByArtItemIdOrderByIdAsc(artItem.getId());

        // 3. 이미지 테이블 생성 및 업데이트 로직
        for (int i = 0; i < itemImgFileList.size(); i++) {
            MultipartFile itemImgFile = itemImgFileList.get(i);

            if (i < artItemImgList.size()) {
                // 기존 이미지가 있으면 업데이트
                artItemImgService.updateItemImg(artItemImgList.get(i).getId(), itemImgFile);
            } else {
                // 기존 이미지가 없는 경우 새로 생성
                ArtItemImg newArtItemImg = new ArtItemImg();
                newArtItemImg.setArtItem(artItem);

                if (!itemImgFile.isEmpty()) {
                    // 이미지가 있다면 저장
                    artItemImgService.saveItemImg(newArtItemImg, itemImgFile);
                } else {
                    // 이미지가 없으면 테이블만 생성
                    newArtItemImg.setOriImgName(null);
                    newArtItemImg.setImgName(null);
                    newArtItemImg.setImgUrl(null);
                    artItemImgRepository.save(newArtItemImg);
                }
            }
        }
        System.out.println("==============아이템 가격 볌위 서비스쪽"+artItem.getPriceRange());

    }


    @Transactional
    public void deleteArtItem(Long artItemId) throws Exception {
        // 1. 삭제할 아트 아이템 로드
        ArtItem artItem = artItemRepository.findById(artItemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다."));

        // 2. 관련 이미지 로드
        List<ArtItemImg> artItemImgList = artItemImgRepository.findByArtItemIdOrderByIdAsc(artItemId);

        // 3. 이미지 삭제 로직
        for (ArtItemImg artItemImg : artItemImgList) {
            // 이미지 삭제 서비스 호출 (파일 삭제와 DB 기록 삭제)
            artItemImgService.deleteItemImg(artItemImg.getId());
        }

        // 4. 아트 아이템 삭제
        artItemRepository.delete(artItem);

        System.out.println("아이템 및 관련 이미지가 삭제되었습니다: ID = " + artItemId);
    }


    @Transactional(readOnly = true) // 쿼리문 실행 읽기만 한다.
    public Page<ArtItem> getAdminArtItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return artItemRepository.getAdminArtItemPage(itemSearchDto,pageable);
    }


    public List<ArtItemImgDto> getArtItemImages(Long artItemId) {
        // ItemImg 데이터 조회
        List<ArtItemImg> artItemImgs = artItemImgRepository.findByArtItemIdOrderByIdAsc(artItemId);

        // ItemImgDto 리스트로 변환하여 반환
        return artItemImgs.stream()
                .map(ArtItemImgDto::of)
                .toList();
    }


}