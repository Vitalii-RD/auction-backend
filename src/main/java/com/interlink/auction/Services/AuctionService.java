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
        Auction auction = auctionRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found with id: " + id));
        auction.getHistory().sort((a, b) -> (int) (a.getBid() - b.getBid()));
        return auction;
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

        if (bidDTO.getUserId().equals(auction.getItem().getOwner().getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Owner cannot create a bid on his own item");
        }

        Bid latestBid = bidRepository.findMaxBidInAuction(auctionId);

        if (latestBid != null) {
            Long increasedBid = getIncreasedBidValue(latestBid.getBid());
            Long increasedMaxBid = getIncreasedBidValue(latestBid.getMaxBid());

            if (bidDTO.getUserId().equals(latestBid.getUser().getId())) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "User`s bid is already the highest");
            } else if (bidDTO.getMaxBid() == bidDTO.getBid()) {
                if (bidDTO.getBid() < increasedBid)  {
                    throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Bid is too small");
                } else if (bidDTO.getBid() < increasedMaxBid) {
                    latestBid.setBid(bidDTO.getBid());
                    bidRepository.save(latestBid);
                    throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Bid is too small");
                }
            } else if(bidDTO.getMaxBid() > increasedMaxBid) {
                if (bidDTO.getBid() < increasedMaxBid) {
                    bidDTO.setBid(increasedMaxBid);
                }
            } else if (bidDTO.getMaxBid() < increasedMaxBid) {
                if (bidDTO.getMaxBid() > increasedBid) {
                    latestBid.setBid(bidDTO.getMaxBid());
                    bidRepository.save(latestBid);
                }
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Bid is too small");
            }

        } else if (auction.getInitialBid() > bidDTO.getBid()) {
            if (auction.getInitialBid() < bidDTO.getMaxBid()) {
                bidDTO.setBid(auction.getInitialBid());
            } else throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Bid is smaller than initial bid");
        }

        auction.addBidToHistory(new Bid(bidDTO.getBid(), bidDTO.getMaxBid(), user, LocalDateTime.now()));
        return auctionRepository.save(auction);
    }

    private static final double INCREASE_RATE = 1.05;
    private static Long getIncreasedBidValue(double bid) {
        double newValue = bid * INCREASE_RATE;
        return (long) newValue;
    }
}
