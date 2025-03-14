package com.shop.service;

import com.shop.entity.ArtItemImg;
import com.shop.entity.RentalItemImg;
import com.shop.repository.RentalItemImgRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

public class RentalItemImgService {
    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final RentalItemImgRepository rentalItemImgRepository;
    private final FileService fileService;






    public void saveItemImg(RentalItemImg rentalItemImg, MultipartFile itemImgFile) throws Exception {







        if (itemImgFile != null && !itemImgFile.isEmpty()) {
            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            String imgUrl = "/images/item/" + imgName;

            rentalItemImg.updateItemImg(oriImgName, imgName, imgUrl);



        } else {



            // 이미지가 없을 경우 비어 있는 상태로 저장
            rentalItemImg.setOriImgName(null);
            rentalItemImg.setImgName(null);
            rentalItemImg.setImgUrl(null);
        }



        rentalItemImgRepository.save(rentalItemImg);
    }

















    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception{

        if(!itemImgFile.isEmpty()){
            RentalItemImg savedRentalItemImg = rentalItemImgRepository.findById(itemImgId). // 아이템 세이브에서 세이브 객체를 뺀다.
                    orElseThrow(EntityNotFoundException::new);
            // 기존에 등록된 상품 이미지 파일이 있는 경우 파일 삭제
            if(!StringUtils.isEmpty(savedRentalItemImg.getImgName())){
                fileService.deleteFile(itemImgLocation+"/"+ savedRentalItemImg.getImgName()); //원래 있던걸 지우고 다시 쓴다.
            }
            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, // 업로드에서 다시 파일을 쓴다.
                    itemImgFile.getBytes());
            String imgUrl = "/images/item/"+imgName; // 해당 주소를 통해 수정된 아이템이 표시됨

            // 변경된 상품 이미지 정보를 세팅
            // 상품 등록을 하는 경우에는 ItemImgRepository.save() 로직을 호출 하지만
            // 호출을 하지 않습니다.
            // svaedItemImg 엔티티는 현재 영속성 상태이다.
            // 그래서 데이터를 변경하는 것만으로 변경을 감지하는 기능이 동작
            // 트랜잭션이 끝날때 update 쿼리가 실행된다.
            // 영속성 상태여야 사용가능

            savedRentalItemImg.updateItemImg(oriImgName, imgName, imgUrl); //업데이트 이미로 바꾸고 영속성 객체에서 감지하고 값 들어온거만 바꿈
        }

    }

    public List<RentalItemImg> getImagesByRentalItemId(Long rentalItemId) {
        return rentalItemImgRepository.findByRentalItemId(rentalItemId);
    }

    public void deleteItemImg(Long itemImgId) throws Exception {
        // 1. 이미지 엔티티 로드
        RentalItemImg savedRentalItemImg = rentalItemImgRepository.findById(itemImgId)
                .orElseThrow(EntityNotFoundException::new);

        // 2. 파일 삭제
        if (!StringUtils.isEmpty(savedRentalItemImg.getImgName())) {
            fileService.deleteFile(itemImgLocation + "/" + savedRentalItemImg.getImgName());
        }

        // 3. DB에서 이미지 엔티티 삭제
        rentalItemImgRepository.delete(savedRentalItemImg);
    }

}
