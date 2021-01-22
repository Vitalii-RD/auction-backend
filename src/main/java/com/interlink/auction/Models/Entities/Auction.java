package com.interlink.auction.Models.Entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "auctions")
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    @Column
    private double initialBid;
    @Column
    private LocalDateTime dateCreated;
    @Column
    private boolean done;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "auction_id", referencedColumnName = "id", nullable = false)
    private List<Bid> history;

    public Auction() { }

    public Auction(Item item, double initialBid, LocalDateTime dateCreated, boolean done) {
        this.item = item;
        this.initialBid = initialBid;
        this.dateCreated = dateCreated;
        this.done = done;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public double getInitialBid() {
        return initialBid;
    }

    public void setInitialBid(double initialBid) {
        this.initialBid = initialBid;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public List<Bid> getHistory() {
        return history;
    }

    public void setHistory(List<Bid> history) {
        this.history = history;
    }

    public void addBidToHistory(Bid bid) {
        this.history.add(bid);
    }
}
