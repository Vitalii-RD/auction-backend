package com.interlink.auction.Services;

import com.interlink.auction.Models.DTO.LoginDTORequest;
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

    public void login(HttpServletResponse response, LoginDTORequest loginDTORequest) {
        User user = userRepository.findByEmailAndPassword(loginDTORequest.getEmail(), loginDTORequest.getPassword());
        if (user != null) {
            System.out.println(user.getId());
            Cookie cookie = new Cookie("id", user.getId().toString());
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exits");
    }



}
