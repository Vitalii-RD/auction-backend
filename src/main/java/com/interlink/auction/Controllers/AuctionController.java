package com.interlink.auction.Controllers;

import com.interlink.auction.Models.DTO.AuctionDTORequest;
import com.interlink.auction.Models.DTO.BidDTORequest;
import com.interlink.auction.Models.Entities.Auction;
import com.interlink.auction.Services.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return auctionService.getAuctionById(id);
    }


    @PostMapping()
    public Auction createAuction(@CookieValue(value = "id", defaultValue = "") String userId,
                                 @RequestBody AuctionDTORequest auction) {
        return auctionService.createAuction(userId, auction);
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
        return auctionService.closeAuction(userId, auctionId, auctionDTORequest);
    }
}
