package com.interlink.auction.Repositories;

import com.interlink.auction.Models.Entities.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {
    @Query(value = "SELECT * FROM bids WHERE bids.auction_id = ?1 ORDER BY bids.bid", nativeQuery = true)
    List<Bid> findAllByAuctionId(long id);

    @Query(value = "SELECT * FROM (SELECT * FROM bids WHERE auction_id = ?1) AS auc " +
            "WHERE bid IN (SELECT MAX(bid) FROM bids WHERE auction_id = ?1);", nativeQuery = true)
    Bid findMaxBidInAuction(Long auctionId);
}
