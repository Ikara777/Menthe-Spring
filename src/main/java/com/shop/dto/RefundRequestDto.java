package com.shop.dto;

import lombok.Data;

@Data
public class RefundRequestDto {
    private String impUid;  // 결제 UID
    private String reason;  // 환불 사유
    private Integer amount; // 환불 금액 (null 가능)
}