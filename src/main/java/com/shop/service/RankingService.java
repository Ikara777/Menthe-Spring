package com.shop.service;

import com.shop.dto.ArtistDto;
import com.shop.dto.RentalDto;
import com.shop.entity.Artist;
import com.shop.entity.Rental;
import com.shop.repository.ArtistRepository;
import com.shop.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RankingService {

    private final ArtistRepository artistRepository;
    private final RentalRepository rentalRepository;

    public List<ArtistDto> getArtistRankings() {
        List<Artist> artists = artistRepository.findAllByOrderByTotalSalesDesc();

        List<ArtistDto> artistDtos = artists.stream()
                .map(artist -> new ArtistDto(artist.getId(), artist.getName(), artist.getImgUrl(), artist.getTotalSales()))
                .collect(Collectors.toList());

        return artistDtos;
    }

    public List<RentalDto> getRentalRankings() {
        List<Rental> rentals = rentalRepository.findAllByOrderByTotalCountDesc();

        List<RentalDto> rentalDtos = rentals.stream()
                .map(rental -> new RentalDto(rental.getId(), rental.getName(), rental.getImgUrl(), rental.getTotalCount()))
                .collect(Collectors.toList());

        return rentalDtos;
    }
}
