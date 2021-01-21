package com.interlink.auction.Services;

import com.interlink.auction.Models.Entities.Auction;
import com.interlink.auction.Models.Entities.Item;
import com.interlink.auction.Models.Entities.User;
import com.interlink.auction.Repositories.AuctionRepository;
import com.interlink.auction.Repositories.ItemRepository;
import com.interlink.auction.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuctionService {
    @Autowired
    AuctionRepository auctionRepository;

    public Auction createAuction() {
        User user = new User("new user", "new", "new");
        Item item = new Item("smth", user);

        Auction auction = new Auction(item, 100, LocalDateTime.now(), false);

        return auctionRepository.save(auction);
    }


}
