package com.shop.dto;


import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatDto {

    private String userId;
    private Long id;
    private String message;
    private String senderId; // 메시지 보낸 사용자 ID
    private String receiverId; // 메시지 받는 관리자 ID
    private String senderRole; // 보낸 사람 역할 (USER, ADMIN)

}
