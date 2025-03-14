package com.shop.dto;

import com.shop.constant.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private int paidAmount;      // 결제 금액 ---
    private String impUid;       // 결제 고유 ID
    private String merchantUid;  // 고유 주문번호
    private String userId;       // 사용자 ID (로그인 고유 식별자)
    private String email;        // 사용자 이메일
    private String tel;          // 사용자 전화번호
    private String address;      // 사용자 주소
    private String name;         // 사용자 이름 (예: 홍길동)
    private PaymentStatus status;
}
