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
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found with id: " + id));
        auction.getHistory().sort(AuctionService::sortHistory);
        return auction;
    }

    public Auction closeAuction(String userId, Long id, AuctionDTORequest auctionDTO) {
        if (userId.equals("")) throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User is not logged in");

        Auction auction = auctionRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found with id: " + id));

        if (!auction.getItem().getOwner().getId().equals(Long.parseLong(userId))) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Wrong user! This auction does not belong to you");
        }

        auction.getHistory().sort(AuctionService::sortHistory);
        auction.setDone(true);
        auctionRepository.save(auction);
        return auction;
    }

    private static int sortHistory(Bid e1, Bid e2) {
        return (int) (e1.getBid() - e2.getBid());
    }

    public void deleteAuction(String userId, Long id) {
        if (userId.equals("")) throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User is not logged in");

        Auction auction = auctionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found with id: " + id));

        if (!auction.getItem().getOwner().getId().equals(Long.parseLong(userId))) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Wrong user! This auction does not belong to you");
        }

        auctionRepository.delete(auction);
    }

    public Auction makeBid(String id, Long auctionId, BidDTORequest bidDTO) {
        if (id.equals("")) throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User is not logged in");

        Long userId = Long.valueOf(id);
        User user =  userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " +  userId));
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found with id: " + auctionId));

        if (userId.equals(auction.getItem().getOwner().getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Owner cannot create a bid on his own item");
        }

        Bid latestBid = bidRepository.findMaxBidInAuction(auctionId);

        if (latestBid != null) {
            Long increasedBid = getIncreasedBidValue(latestBid.getBid());
            Long increasedMaxBid = getIncreasedBidValue(latestBid.getMaxBid());

            if (userId.equals(latestBid.getUser().getId())) {
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
