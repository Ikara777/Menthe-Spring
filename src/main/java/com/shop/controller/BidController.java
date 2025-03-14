package com.shop.controller;

import com.shop.entity.ArtItem;
import com.shop.entity.Bid;
import com.shop.repository.ArtItemRepository;
import com.shop.repository.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BidController {

    @Autowired
    private ArtItemRepository artItemRepository;

    @Autowired
    private BidRepository bidRepository;

    @GetMapping("/api/artItem/{artItemId}/bids")
    public List<Bid> getBidsByArtItem(@PathVariable Long artItemId) {
        ArtItem artItem = artItemRepository.findById(artItemId).orElseThrow(() -> new RuntimeException("ArtItem not found"));
        return bidRepository.findByArtItem(artItem);
    }

}
