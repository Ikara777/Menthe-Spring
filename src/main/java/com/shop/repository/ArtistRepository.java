package com.shop.repository;

import com.shop.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    List<Artist> findAllByOrderByTotalSalesDesc();
}
