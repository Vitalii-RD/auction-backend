package com.interlink.auction.Services;

import com.interlink.auction.Models.DTO.AuctionDTORequest;
import com.interlink.auction.Models.DTO.BidDTORequest;
import com.interlink.auction.Models.Entities.Auction;
import com.interlink.auction.Models.Entities.Bid;
import com.interlink.auction.Models.Entities.Item;
import com.interlink.auction.Models.Entities.User;
import com.interlink.auction.Repositories.AuctionRepository;
import com.interlink.auction.Repositories.BidRepository;
import com.interlink.auction.Repositories.ItemRepository;
import com.interlink.auction.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuctionService {
    @Autowired
    AuctionRepository auctionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BidRepository bidRepository;

    public Auction createAuction(AuctionDTORequest auctionDTO) {
        User owner = userRepository.findById(auctionDTO.getOwnerId())
            .orElseThrow(
                () -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Account not found with id: " + auctionDTO.getOwnerId())
            );

        Item item = new Item(auctionDTO.getItemName(), owner);
        Auction result = new Auction(item, auctionDTO.getInitialBid(), LocalDateTime.now(), false);
        return auctionRepository.save(result);
    }

    public List<Auction> getAll() {
        List<Auction> auctions = auctionRepository.findAll();
        auctions.forEach(auction -> auction.getHistory().sort((a, b) -> (int) (a.getBid() - b.getBid())));
        return  auctions;
    }

    public Auction getAuctionById(Long id) {
        return auctionRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found with id: " + id));
    }

    public void deleteAuction(Long id) {
        Auction auction = auctionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found with id: " + id));
        auctionRepository.delete(auction);
    }

    public Auction makeBid(Long auctionId, BidDTORequest bidDTO) {
        User user =  userRepository.findById(bidDTO.getUserId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + bidDTO.getUserId()));
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found with id: " + auctionId));
        Bid maxBid = bidRepository.findMaxBidInAuction(auctionId);

        if (maxBid != null) {
            if (bidDTO.getUserId().equals(maxBid.getUser().getId())) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "User`s bid is already the highest");
            } else if (bidDTO.getUserId().equals(auction.getItem().getOwner().getId())) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Owner cannot create a bid on his own item");
            } else if (bidDTO.getBid() < AuctionService.getIncreasedBidValue(maxBid.getBid())) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Bid is too small");
            }
        }

        auction.addBidToHistory(new Bid(bidDTO.getBid(), user, LocalDateTime.now()));
        return auctionRepository.save(auction);
    }

    private static final double INCREASE_RATE = 1.05;

    private static Long getIncreasedBidValue(double bid) {
        double newValue = bid * INCREASE_RATE;
        return (long) newValue;
    }
}
