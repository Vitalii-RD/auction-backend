package com.interlink.auction.Services;

import com.interlink.auction.Models.DTO.LoginDTORequest;
import com.interlink.auction.Models.DTO.UserDTOResponse;
import com.interlink.auction.Models.Entities.User;
import com.interlink.auction.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {
    @Autowired
    UserRepository userRepository;

    public UserDTOResponse login(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }
}
