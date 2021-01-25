package com.interlink.auction.Repositories;

import com.interlink.auction.Models.DTO.UserDTOResponse;
import com.interlink.auction.Models.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    UserDTOResponse findByEmailAndPassword(String email, String password);
}
