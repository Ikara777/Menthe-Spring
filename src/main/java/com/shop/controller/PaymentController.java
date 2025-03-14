package com.shop.controller;


import com.shop.constant.PaymentStatus;
import com.shop.dto.PaymentRequest;
import com.shop.dto.PaymentResponse;
import com.shop.entity.Member;
import com.shop.service.MemberService;
import com.shop.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;

@Controller
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final MemberService memberService;

    @Autowired
    public PaymentController(PaymentService paymentService, MemberService memberService) {
        this.paymentService = paymentService;
        this.memberService = memberService;
    }


        // UI 렌더링용 GET 요청
    @GetMapping("/verify-page")
    public String showVerifyPage(Model model, Principal principal) {
        // Principal에서 로그인된 사용자 ID 가져오기
        String userId = principal.getName(); // 로그인한 사용자 ID (예: ads1313)

        // 사용자 정보를 조회 (예: MemberService 활용)
        Member member = memberService.getMemberById(userId); // MemberService에서 사용자 정보 조회


        PaymentRequest paymentRequest = new PaymentRequest(); // PaymentRequest 객체 생성 및 사용자 정보 설정
        paymentRequest.setUserId(member.getUserId()); // 회원 ID
        paymentRequest.setMerchantUid("ORD" + System.currentTimeMillis()); // 고유 주문번호 생성

        model.addAttribute("member", member.getId());
        // Model에 PaymentRequest 추가
        model.addAttribute("paymentRequest", paymentRequest);
        model.addAttribute("UserId", member.getId()); // 유저 아이디
        model.addAttribute("email", member.getEmail()); // 이메일
        model.addAttribute("name", member.getName()); // 이름
        model.addAttribute("tel", member.getTel()); // 전화번호
        model.addAttribute("add", member.getAddress()); // 주소

        return "member/payment";
    }


    // 결제 검증용 POST 요청
    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            logger.info("결제 요청 정보: " + paymentRequest);

            // 포트원에서 에러가 없음을 신뢰
            boolean hasError = paymentRequest.getImpUid() == null || paymentRequest.getMerchantUid() == null;

            if (hasError) {
                logger.error("결제 요청에 오류가 있습니다.");
                return ResponseEntity.badRequest().body("결제 검증 실패");
            }

            paymentService.savePayment(paymentRequest); // 결제 정보 저장
            return ResponseEntity.ok("결제가 성공적으로 처리되었습니다.");

        } catch (Exception e) {
            logger.error("결제 처리 중 오류 발생", e);
            return ResponseEntity.status(500).body("결제 처리 중 오류 발생");
        }
    }



}