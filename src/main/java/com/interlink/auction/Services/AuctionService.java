package com.interlink.auction.Services;

import com.interlink.auction.Exceptions.InvalidBidValueException;
import com.interlink.auction.Exceptions.WrongUserException;
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

    public Auction createAuction(String userId, Auction auction) {
        User owner = userRepository.findById(Long.parseLong(userId))
            .orElseThrow(NullPointerException::new);
        auction.getItem().setOwner(owner);
        return auctionRepository.save(auction);
    }

    public List<Auction> getAll() {
        List<Auction> auctions = auctionRepository.findAll();
        auctions.forEach(auction -> auction.getHistory().sort(AuctionService::sortHistory));
        return  auctions;
    }

    public Auction getAuctionById(Long id) {
        Auction auction = auctionRepository
            .findById(id)
            .orElseThrow(NullPointerException::new);
        auction.getHistory().sort(AuctionService::sortHistory);
        return auction;
    }

    public Auction closeAuction(String userId, Long id, AuctionDTORequest auctionDTO) throws WrongUserException {
        Auction auction = auctionRepository
                .findById(id)
                .orElseThrow(NullPointerException::new);

        if (!auction.getItem().getOwner().getId().equals(Long.parseLong(userId)))
            throw new WrongUserException("Wrong user! This auction does not belong to you");

        auction.getHistory().sort(AuctionService::sortHistory);
        auction.setDone(true);
        auctionRepository.save(auction);
        return auction;
    }

    private static int sortHistory(Bid e1, Bid e2) {
        return (int) (e1.getBid() - e2.getBid());
    }

    public void deleteAuction(String userId, Long id) throws WrongUserException {
        Auction auction = auctionRepository.findById(id)
            .orElseThrow(NullPointerException::new);

        if (!auction.getItem().getOwner().getId().equals(Long.parseLong(userId)))
            throw new WrongUserException("Wrong user! This auction does not belong to you");

        auctionRepository.delete(auction);
    }

    public Auction makeBid(String id, Long auctionId, Bid bid)
            throws WrongUserException, InvalidBidValueException {
        Long userId = Long.valueOf(id);
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("User not found with id: " +  userId));
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new NullPointerException("Auction not found with id: " + auctionId));

        if (userId.equals(auction.getItem().getOwner().getId()))
            throw new WrongUserException("Owner cannot create a bid on his own item");

        Bid latestBid = bidRepository.findMaxBidInAuction(auctionId);

        if (latestBid != null) {
            Long increasedBid = getIncreasedBidValue(latestBid.getBid());
            Long increasedMaxBid = getIncreasedBidValue(latestBid.getMaxBid());

            if (userId.equals(latestBid.getUser().getId())) {
                throw new  InvalidBidValueException("User`s bid is already the highest");
            } else if (bid.getMaxBid() == bid.getBid()) {
                if (bid.getBid() < increasedBid)  {
                    throw new InvalidBidValueException("Bid is too small");
                } else if (bid.getBid() < increasedMaxBid) {
                    latestBid.setBid(bid.getBid());
                    bidRepository.save(latestBid);
                    throw new InvalidBidValueException("Bid is too small");
                }
            } else if(bid.getMaxBid() > increasedMaxBid) {
                if (bid.getBid() < increasedMaxBid) {
                    bid.setBid(increasedMaxBid);
                }
            } else if (bid.getMaxBid() < increasedMaxBid) {
                if (bid.getMaxBid() > increasedBid) {
                    latestBid.setBid(bid.getMaxBid());
                    bidRepository.save(latestBid);
                }
                throw new InvalidBidValueException("Bid is too small");
            }

        } else if (auction.getInitialBid() > bid.getBid()) {
            if (auction.getInitialBid() < bid.getMaxBid()) {
                bid.setBid(auction.getInitialBid());
            } else throw new InvalidBidValueException("Bid is smaller than initial bid");
        }

        auction.addBidToHistory(new Bid(bid.getBid(), bid.getMaxBid(), user, LocalDateTime.now()));
        return auctionRepository.save(auction);
    }

    private static final double INCREASE_RATE = 1.05;
    private static Long getIncreasedBidValue(double bid) {
        long newValue = Math.round(bid * INCREASE_RATE);
        int len = Long.toString(newValue).length();
        if (1 < len && len <= 3) {
            newValue = (long) Math.ceil((double)newValue / 10) * 10;
        } else if (len > 3){
            newValue = (long) Math.ceil((double)newValue / 100) * 100;
        }
        return newValue;
    }
}
