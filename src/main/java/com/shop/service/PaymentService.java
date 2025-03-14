package com.shop.service;


import com.shop.constant.PaymentStatus;
import com.shop.dto.PaymentRequest;
import com.shop.dto.PaymentResponse;
import com.shop.entity.Member;
import com.shop.entity.Payment;
import com.shop.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import org.springframework.data.domain.Pageable;

import static com.shop.constant.PaymentStatus.CANCEL;
import static com.shop.constant.PaymentStatus.PAID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public PaymentResponse savePayment(PaymentRequest paymentRequest) {
        PaymentResponse response = new PaymentResponse();

        try {
            // PaymentRequest를 Payment 엔티티로 변환
            Payment payment = new Payment();
            payment.setImpUid(paymentRequest.getImpUid());
            payment.setMerchantUid("Cache-plus");
            payment.setPaidAmount(paymentRequest.getPaidAmount());
            payment.setBuyerName(paymentRequest.getName()); // 필요시 추가로 설정
            payment.setBuyerEmail(paymentRequest.getEmail()); // 필요시 추가로 설정
            payment.setBuyerTel(paymentRequest.getTel());
            payment.setStatus(PAID);
            payment.setBuyerAddr(paymentRequest.getAddress());
            payment.setPaidAt(java.time.LocalDateTime.now()); // 결제 시간 설정

            // 데이터베이스에 저장
            paymentRepository.save(payment);

            response.setSuccess(true);
            response.setMessage("결제가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("결제 저장 중 오류 발생: " + e.getMessage());
        }

        return response;
    }


    public PaymentResponse updatePaymentStatusToCancel(String impUid) {
        PaymentResponse response = new PaymentResponse();

        try {
            // impUid로 Payment 검색
            Payment payment = paymentRepository.findByImpUid(impUid)
                    .orElseThrow(() -> new RuntimeException("해당 merchant_uid를 찾을 수 없습니다: " + impUid));
            System.out.println("검색된 Payment 객체: " + payment); // 디버깅용 출력

            // 결제 상태를 CANCEL로 변경
            payment.setStatus(CANCEL);
            System.out.println("변경 후 상태: " + payment.getStatus());

            // 업데이트
            paymentRepository.save(payment);
            System.out.println("결제 상태가 CANCEL로 저장되었습니다.");

            response.setSuccess(true);
            response.setMessage("결제 상태가 CANCEL로 업데이트되었습니다.");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("결제 상태 업데이트 중 오류 발생: " + e.getMessage());
        }

        return response;
    }





        // 이메일과 merchant_uid로 합계 계산
        public Integer getTotalAmountByEmailAndMerchantUid(String email, String merchantUid) {
            // Repository에서 합산 쿼리 호출
            return paymentRepository.sumPaidAmountByEmailAndMerchantUid(email, merchantUid).orElse(0);
        }


    public Page<Payment> getPaymentList(String Email, Pageable pageable,String merchantUid) {
        return paymentRepository.findByBuyerEmailAndMerchantUidCustom(Email, merchantUid, pageable);
    }


}
