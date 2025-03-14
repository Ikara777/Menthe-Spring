package com.shop.controller;

import com.shop.constant.Role;
import com.shop.entity.ChatMessage;
import com.shop.entity.Member;
import com.shop.service.ChatService;
import com.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final MemberService memberService;
    private final ChatService chatService;

    @GetMapping("/chat/popup/{userId}")
    public ResponseEntity<?> chatPopup(@PathVariable String userId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        Member member = memberService.getMemberById(principal.getName());
        List<ChatMessage> chatLogs = chatService.getChatHistory(principal.getName());

        System.out.println("==========////////" + userId);
        System.out.println("------챗로그 -----");
        for (ChatMessage message : chatLogs) {
            System.out.println("Sender: " + message.getSenderId() + ", Message: " + message.getMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("username", userId);
        response.put("member", member);
        response.put("chatLogs", chatLogs);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/chat/adminPopup/{userId}")
    public ResponseEntity<?> chatAdminPopup(@PathVariable String userId) {

        Member member = memberService.getMemberById(userId);
        List<ChatMessage> chatLogs = chatService.getChatHistory(userId);

        System.out.println("==========////////" + userId);
        System.out.println("------챗로그 -----");
        for (ChatMessage message : chatLogs) {
            System.out.println("Sender: " + message.getSenderId() + ", Message: " + message.getMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("username", userId);
        response.put("member", member);
        response.put("chatLogs", chatLogs);

        return ResponseEntity.ok(response);
    }



    @PostMapping("/chat/popup/{userId}")
    @ResponseBody
    public ResponseEntity<?> sendPopupMessage(
            @PathVariable String userId,
            @RequestParam String message,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        // 로그인한 사용자 정보 가져오기
        Member sender = memberService.getMemberById(principal.getName());
        Role senderRole = sender.getRole();

        // 메시지 저장 로직 호출
        chatService.saveMessage(sender.getName(), userId, message, senderRole);

        // 저장 후 갱신된 대화 로그
        List<ChatMessage> chatLogs = chatService.getChatHistory(userId);

        // 클라이언트가 필요로 하는 정보 반환
        Map<String, Object> response = new HashMap<>();
        response.put("senderId", sender.getName());
        response.put("message", message);
        response.put("chatLogs", chatLogs); // 필요하면 전체 로그를 보냄


        return ResponseEntity.ok(response);
    }



    @GetMapping("/chat/{userId}")
    public String chatLog(@PathVariable String userId, Model model) {
        // 현재 로그인된 사용자 아이디
        String username = userId;
        Member member = memberService.getMemberById(username);
        List<ChatMessage> chatLogs = chatService.getChatHistory(username);


        // chatLogs 변수 테스트용
        for (ChatMessage chatMessage : chatLogs) {
            System.out.println("=================================");
            System.out.println("ID: " + chatMessage.getId());
            System.out.println("User ID: " + chatMessage.getUserId());
            System.out.println("Message: " + chatMessage.getMessage());
            System.out.println("Sender ID: " + chatMessage.getSenderId());
            System.out.println("Receiver ID: " + chatMessage.getReceiverId());
            System.out.println("Sender Role: " + chatMessage.getSenderRole());
            System.out.println("Timestamp: " + chatMessage.getTimestamp());
            System.out.println("=================================");
        }


        System.out.println("===========chat 테스트============");
        System.out.println(member.getId());
        System.out.println(member.getName());
        System.out.println(member.getEmail());
        System.out.println(username);
        System.out.println("===========chat 테스트============");

        // 사용자 정보를 뷰에 전달
        model.addAttribute("username", username);
        model.addAttribute("member", member);
        model.addAttribute("chatLogs", chatLogs);

        return "member/chat"; // chat.html 로 이동
    }


    @GetMapping("/chat/list")
    public String chatList(Model model) {

        Map<String, String> chatUserNames = chatService.getChatUserNames();

        model.addAttribute("chatUserNames", chatUserNames);
        return "member/chatList"; // chat.html 로 이동
    }


    @PostMapping("/chat/member/{userId}")
    public String sendMessage(
            @PathVariable String userId,
            @RequestParam String message,
            Principal principal) {
        System.out.println("POST 요청 도착");
        System.out.println("UserId: " + userId);
        System.out.println("Message: " + message);

        // 대화하는 유저를 기준으로 맴버검색
        Member member = memberService.getMemberById(userId);

        // 현재 로그인한 사람을 기준으로 정함
        Member Post = memberService.getMemberById(principal.getName());
        String sander = Post.getName();
        Role sanderRole = Post.getRole();

        // 메시지 저장 로직 호출
        chatService.saveMessage(sander, userId, message, sanderRole);
        return "redirect:/chat/" + userId;
    }


}