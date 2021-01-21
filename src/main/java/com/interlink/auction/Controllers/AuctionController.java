package com.interlink.auction.Controllers;

import com.interlink.auction.Models.DTO.AuctionDTORequest;
import com.interlink.auction.Models.Entities.Auction;
import com.interlink.auction.Services.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/auctions")
public class AuctionController {
    @Autowired
    private AuctionService auctionService;

    @GetMapping
    List<Auction> getAll() {
        return auctionService.getAll();
    }

    @GetMapping("/{id}")
    Auction getAuction(@PathVariable("id") Long id) {
        return auctionService.getAuctionById(id);
    }

    @PostMapping()
    public Auction createAuction(@RequestBody AuctionDTORequest auction) {
        return auctionService.createAuction(auction);
    }

    @DeleteMapping("/{id}")
    void deleteAuction(@PathVariable("id") Long id) {
        auctionService.deleteAuction(id);
    }
}
