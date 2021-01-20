package com.interlink.auction.Repositories;

import com.interlink.auction.Models.Entities.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<Auction> findAllByDoneIsFalseOrderByDateCreated();
}
