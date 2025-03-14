package com.shop.service;

import com.shop.constant.ItemSellStatus;
import com.shop.constant.PaymentStatus;
import com.shop.dto.BidDto;
import com.shop.entity.*;
import com.shop.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BidService {

    private final ArtItemRepository artItemRepository;
    private final BidRepository bidRepository;
    private final PurchaseRepository purchaseRepository;
    private final CashSummaryRepository cashSummaryRepository;
    private final PaymentService paymentService;
    private final RentRepository rentRepository;
    private final CashSummaryService cashSummaryService;

    public BidService(ArtItemRepository artItemRepository, BidRepository bidRepository, PurchaseRepository purchaseRepository, CashSummaryRepository cashSummaryRepository,
                      PaymentService paymentService, RentRepository rentRepository, CashSummaryService cashSummaryService) {
        this.artItemRepository = artItemRepository;
        this.bidRepository = bidRepository;
        this.purchaseRepository = purchaseRepository;
        this.cashSummaryRepository = cashSummaryRepository;
        this.paymentService = paymentService;
        this.rentRepository = rentRepository;
        this.cashSummaryService = cashSummaryService;
    }

    // 경매 응찰하기
    @Transactional
    public Bid submitBid(BidDto bidDto, Member member) {
        // 경매 아이템을 가져옵니다.
        ArtItem artItem = artItemRepository.findById(bidDto.getItemId())
                .orElseThrow(() -> new RuntimeException("경매 아이템을 찾을 수 없습니다."));

        // 경매가 진행 중인 경우에만 응찰 가능
        if (artItem.getItemSellStatus() != ItemSellStatus.SELL) {
            throw new RuntimeException("경매가 종료된 아이템입니다.");
        }

        // 응찰 금액이 유효한지 확인
        if (bidDto.getBidAmount() < artItem.getStartPrice()) {
            throw new RuntimeException("응찰 금액이 시작가보다 낮습니다.");
        }

        // 새로운 응찰 생성
        Bid bid = new Bid();
        bid.setArtItem(artItem);
        bid.setBidAmount(bidDto.getBidAmount());
        bid.setMember(member); // Member 객체 사용
        bid.setBidTime(LocalDateTime.now());

        // 최대 응찰 금액을 계산
        Integer maxAmount = bidRepository.findMaxAmountByArtId(artItem.getId());

        // maxAmount가 없다면, 시작 가격을 기본값으로 설정
        if (maxAmount == null) {
            maxAmount = artItem.getStartPrice();
        }

        // 응찰 금액이 이전 최대 금액보다 큰지 확인하고, 그렇다면 maxAmount를 업데이트
        if (bidDto.getBidAmount() >= maxAmount) {
            maxAmount = bidDto.getBidAmount();
        } else {
            throw new RuntimeException("응찰 금액이 이전 응찰 금액보다 낮습니다.");
        }

        // 새로운 응찰의 최대 금액을 설정
        bid.setMaxAmount(maxAmount);

        // 응찰 저장
        return bidRepository.save(bid);
    }

//    ******************************낙찰****************

    public List<Bid> getSuccessfulBidsByMember(Long memberId) {
        // 현재 시간 구하기
        LocalDateTime now = LocalDateTime.now();

        // 경매 종료 시간이 현재 시간보다 이전인 경매 아이템들을 가져오기
        List<ArtItem> artItems = artItemRepository.findByEventEndBefore(now);

        // 낙찰된 경매만 필터링
        List<Bid> successfulBids = artItems.stream()
                .map(artItem -> {
                    // 해당 경매 아이템에 대해 가장 높은 응찰 금액을 제시한 Bid 찾기
                    Bid highestBid = bidRepository.findTopByArtItemOrderByBidAmountDesc(artItem);

                    // 경매 종료 후 가장 높은 금액을 제시한 Bid가 해당 사용자의 응찰인 경우에만 반환
                    if (highestBid != null && highestBid.getMember().getId().equals(memberId)) {
                        return highestBid; // 낙찰자
                    }
                    return null; // 낙찰자가 아니면 null
                })
                .filter(Objects::nonNull) // null 필터링
                .collect(Collectors.toList());

        return successfulBids;
    }

    //    ********** 구매확정 ******************
    public void confirmPurchase(Long memberId, Long bidId) {
        // Bid 찾기
        Optional<Bid> bidOpt = bidRepository.findById(bidId);
        if (bidOpt.isPresent()) {
            Bid bid = bidOpt.get();

            // 해당 Bid의 최고 응찰자 확인 (현재 사용자가 최고 응찰자인지)
            if (!bid.getMember().getId().equals(memberId)) {
                throw new IllegalArgumentException("구매 확정 권한이 없습니다.");
            }

            // CashSummary에서 해당 사용자의 정보 찾기
            Optional<CashSummary> cashSummaryOpt = cashSummaryRepository.findByEmail(bid.getMember().getEmail());
            if (cashSummaryOpt.isEmpty()) {
                throw new IllegalArgumentException("CashSummary 정보가 없습니다.");
            }

            // 첫 번째 CashSummary 객체를 가져오기
            CashSummary cashSummary = cashSummaryOpt.get();

            // 잔액 확인 (구매 확정 전에 잔액이 충분한지 체크)
            if (cashSummary.getTotalAmount() < bid.getBidAmount()) {
                throw new IllegalArgumentException("잔액이 부족합니다.");
            }

            // 1. CashSummary에서 금액 차감
            // bidAmount 만큼 CashSummary에서 잔액을 차감
            cashSummary.setTotalAmount(cashSummary.getTotalAmount() - bid.getBidAmount());
            cashSummaryRepository.save(cashSummary);  // DB에 반영

            System.out.println(cashSummary);

            // 2. ArtItem의 상태를 SOLD_OUT으로 변경
            ArtItem artItem = bid.getArtItem();
            artItem.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            artItemRepository.save(artItem);  // DB에 반영

            // 3. Bid의 구매 확정 상태를 true로 설정
            bid.setPurchaseConfirmed(true);
            bidRepository.save(bid);  // DB에 반영

            // 4. 새로운 Purchase 객체 생성
            Purchase purchase = new Purchase();
            purchase.setMember(bid.getMember());
            purchase.setArtItem(artItem);
            purchase.setBidAmount(bid.getBidAmount());
            purchase.setPaymentStatus(PaymentStatus.PAID);
            purchase.setPostcode(bid.getMember().getPostcode()); // Member에서 주소정보 가져오기
            purchase.setAddress(bid.getMember().getAddress());
            purchase.setTel(bid.getMember().getTel());

            purchaseRepository.save(purchase);  // DB에 반영

            Integer paymentAmount = paymentService.getTotalAmountByEmailAndMerchantUid(bid.getMember().getEmail(), "Cache-plus");
            Integer rentAmount =  rentRepository.sumRentAmountByMemberId(bid.getMember().getId()).orElse(0);
            Integer purchaseAmount = bidRepository.sumBidAmountByMemberId(bid.getMember().getId()).orElse(0);
            Integer totalAmount = paymentAmount - rentAmount - purchaseAmount;

            cashSummaryService.saveOrUpdateCashSummary(bid.getMember().getEmail(), bid.getMember().getName(), totalAmount);

        } else {
            throw new IllegalArgumentException("Bid not found.");
        }
    }

    public Integer getTotalBidAmountByMemberId(Long memberId) {
        // Repository에서 합산 쿼리 호출
        return bidRepository.sumBidAmountByMemberId( memberId ).orElse(0);
    }

}