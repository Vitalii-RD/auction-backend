package com.interlink.auction.Controllers;

import com.interlink.auction.Exceptions.WrongUserException;
import com.interlink.auction.Models.DTO.AuctionDTORequest;
import com.interlink.auction.Models.DTO.BidDTORequest;
import com.interlink.auction.Models.Entities.Auction;
import com.interlink.auction.Models.Entities.Item;
import com.interlink.auction.Services.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/auctions")
public class AuctionController {
    @Autowired
    private AuctionService auctionService;

    @GetMapping
    public List<Auction> getAll() {
        return auctionService.getAll();
    }

    @GetMapping("/{id}")
    public Auction getAuction(@PathVariable("id") Long id) {
        try {
            return auctionService.getAuctionById(id);
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found with id: " + id);
        }
    }


    @PostMapping()
    public Auction createAuction(@CookieValue(value = "id", defaultValue = "") String userId,
                                 @RequestBody AuctionDTORequest auctionDTO) {
        if (userId.equals(""))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not logged in");
        try {
            Item item = new Item();
            item.setTitle(auctionDTO.getItemName());
            Auction auction = new Auction(item, auctionDTO.getInitialBid(), LocalDateTime.now(), false);
            return auctionService.createAuction(userId, auction);
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found with id: " + userId);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteAuction(@CookieValue(value = "id", defaultValue = "") String userId,
                       @PathVariable("id") Long auctionId) {
        auctionService.deleteAuction(userId, auctionId);
    }

    @PostMapping("/{id}/bids")
    public Auction makeBid(@CookieValue(value = "id", defaultValue = "") String userId,
                           @PathVariable("id") Long auctionId,
                           @RequestBody BidDTORequest bidDTORequest) {
        return auctionService.makeBid(userId, auctionId, bidDTORequest);
    }

    @PutMapping("/{id}")
    public Auction closeAuction(@CookieValue(value = "id", defaultValue = "") String userId,
                                @PathVariable("id") Long auctionId,
                                @RequestBody AuctionDTORequest auctionDTORequest) {
        if (userId.equals("")) throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User is not logged in");
        try {
            return auctionService.closeAuction(userId, auctionId, auctionDTORequest);
        } catch (NullPointerException e ) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found with id: " + userId);
        } catch (WrongUserException e ) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        }
    }
}
