package com.interlink.auction.Repositories;

import com.interlink.auction.Models.Entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
