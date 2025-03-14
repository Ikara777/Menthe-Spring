package com.shop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageDto {

    private String senderId; // 보낸 사람 ID
    private String receiverId; // 받는 사람 ID
    private String message; // 메시지 내용
}
