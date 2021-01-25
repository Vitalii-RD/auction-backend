package com.interlink.auction.Models.DTO;

public class BidDTORequest {
    private double bid;
    private double maxBid;

    public BidDTORequest(double bid, double maxBid) {
        this.bid = bid;
        this.maxBid = maxBid;
    }

    public BidDTORequest() { }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public double getMaxBid() {
        return maxBid;
    }

    public void setMaxBid(double maxBid) {
        this.maxBid = maxBid;
    }
}
