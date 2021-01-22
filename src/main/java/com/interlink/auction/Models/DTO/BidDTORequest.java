package com.interlink.auction.Models.DTO;

public class BidDTORequest {
    private double bid;
    private double maxBid;
    private Long userId;

    public BidDTORequest(double bid, double maxBid, Long userId) {
        this.bid = bid;
        this.maxBid = maxBid;
        this.userId = userId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
