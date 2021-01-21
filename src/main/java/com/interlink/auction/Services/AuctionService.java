package com.interlink.auction.Services;

import com.interlink.auction.Repositories.AuctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuctionService {
    @Autowired
    AuctionRepository auctionRepository;

}
