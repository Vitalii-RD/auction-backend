package com.interlink.auction.Models.DTO;

public class AuctionDTORequest {
    private String itemName;
    private double initialBid;
    private boolean done;

    public AuctionDTORequest() {
    }

    public AuctionDTORequest(String itemName, double initialBid, boolean done) {
        this.itemName = itemName;
        this.initialBid = initialBid;
        this.done = done;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getInitialBid() {
        return initialBid;
    }

    public void setInitialBid(double initialBid) {
        this.initialBid = initialBid;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
