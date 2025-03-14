package com.shop.entity;

import com.shop.constant.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id; // 메시지 번호

        @Column(nullable = false)
        private String userId; // 사용자 ID

        @Column(nullable = false)
        private String message; // 메시지 내용

        private String senderId; // 보낸 사람 ID
        private String receiverId; // 받은 사람 ID (관리자)

        @Enumerated(EnumType.STRING)
        private Role senderRole; // 보낸 사람 역할 (USER, ADMIN)

        @Column(nullable = false)
        private LocalDateTime timestamp; // 보낸 시간


}
