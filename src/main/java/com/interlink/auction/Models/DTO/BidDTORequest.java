package com.interlink.auction.Models.DTO;

public class BidDTORequest {
    private double bid;
    private Long userId;

    public BidDTORequest(double bid, Long userId) {
        this.bid = bid;
        this.userId = userId;
    }

    public BidDTORequest() { }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
