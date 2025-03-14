package com.shop.service;

import com.shop.constant.Role;
import com.shop.entity.ChatMessage;
import com.shop.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final MemberService memberService;

    // 메시지 저장
    public void saveMessage(String sander, String userId, String massage, Role sanderRole ) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId(sander);
        chatMessage.setUserId(userId);
        chatMessage.setSenderRole(sanderRole);
        chatMessage.setMessage(massage);
        chatMessage.setTimestamp(LocalDateTime.now()); // 현재 시간 설정
        chatMessageRepository.save(chatMessage);
    }

    // 특정 사용자와의 대화 내용 조회
    public List<ChatMessage> getChatHistory(String userId) {
        return chatMessageRepository.findByUserId(userId);
    }

    // 중복 없이 모든 userId를 가지고 오는 로직
    public List<String> getDistinctChatUserIds() {
        return chatMessageRepository.findDistinctUserIds();
    }


    public Map<String, String> getChatUserNames() {
        // chat_message 테이블에서 중복 없는 userId 가져오기
        List<String> userIdList = chatMessageRepository.findDistinctUserIds();

        // userId 리스트로 이름 매핑 가져오기
        return memberService.getNamesByUserIds(userIdList);
    }

}