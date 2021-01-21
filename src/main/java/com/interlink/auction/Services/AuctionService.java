package com.interlink.auction.Services;

import com.interlink.auction.Models.DTO.AuctionDTORequest;
import com.interlink.auction.Models.Entities.Auction;
import com.interlink.auction.Models.Entities.Item;
import com.interlink.auction.Models.Entities.User;
import com.interlink.auction.Repositories.AuctionRepository;
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
        return auctionRepository.findAll();
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
}
