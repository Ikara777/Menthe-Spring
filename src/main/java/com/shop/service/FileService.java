package com.shop.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

// 상품 등록을 할때 이미지를 서버 쪽으로 옮기는 파일
@Service
@Log
public class FileService {

    public String uploadFile (String uploadPath, String originalFileName, byte[] fileData)
            throws Exception{
        UUID uuid = UUID.randomUUID(); //랜덤으로 UUID 생성
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid.toString() + extension;
        String fileUploadFullUrl = uploadPath+"/"+savedFileName;
        System.out.println(fileUploadFullUrl);
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        fos.write(fileData);
        fos.close();
        return savedFileName;
    }

    public void deleteFile(String filePath) throws Exception{
        File deleteFile = new File(filePath);

        if(deleteFile.exists()) {
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        }else {
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
