package com.shop.service;


import com.shop.constant.PaymentStatus;
import com.shop.dto.RentDto;
import com.shop.entity.Member;
import com.shop.entity.Rent;
import com.shop.entity.RentalItem;
import com.shop.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RentService {

    private final RentalItemRepository rentalItemRepository;
    private final RentRepository rentRepository;
    private final PaymentService paymentService;
    private final BidRepository bidRepository;
    private final CashSummaryService cashSummaryService;

    public RentService(RentalItemRepository rentalItemRepository, RentRepository rentRepository,
                       PaymentService paymentService, BidRepository bidRepository, CashSummaryService cashSummaryService){
        this.rentalItemRepository = rentalItemRepository;
        this.rentRepository = rentRepository;
        this.paymentService = paymentService;
        this.bidRepository = bidRepository;
        this.cashSummaryService = cashSummaryService;
    }

    @Transactional
    public Rent submitRent(RentDto rentDto, Member member){
        RentalItem rentalItem = rentalItemRepository.findById(rentDto.getItemId())
                .orElseThrow(() -> new RuntimeException("렌탈 아이템을 찾을 수 없습니다."));



        Rent rent = new Rent();
        rent.setRentalItem(rentalItem);
        rent.setRentAmount(rentalItem.getPrice());
        rent.setMember(member);
        rent.setRentTime(LocalDateTime.now());
        rent.setStatus(PaymentStatus.PAID);


        Integer paymentAmount = paymentService.getTotalAmountByEmailAndMerchantUid(member.getEmail(), "Cache-plus");
        Integer rentAmount =  rentRepository.sumRentAmountByMemberId(member.getId()).orElse(0);
        Integer purchaseAmount = bidRepository.sumBidAmountByMemberId(member.getId()).orElse(0);
        Integer totalAmount = paymentAmount - rentAmount - purchaseAmount;


        cashSummaryService.saveOrUpdateCashSummary(member.getEmail(), member.getName(), totalAmount);


        return rentRepository.save(rent);
    }

    public Integer getTotalAmountByMemberId(Long memberId) {
        // Repository에서 합산 쿼리 호출
        return rentRepository.sumRentAmountByMemberId( memberId ).orElse(0);
    }


}
