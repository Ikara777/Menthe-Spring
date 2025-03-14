package com.shop.repository;

import com.shop.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>  {

    // 특정 사용자와 관련된 메시지 조회
    List<ChatMessage> findByUserId(String UserId);

    @Query("SELECT DISTINCT cm.userId FROM ChatMessage cm")
    List<String> findDistinctUserIds();

}
