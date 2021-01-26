package com.interlink.auction.Controllers;

import com.interlink.auction.Models.DTO.LoginDTORequest;
import com.interlink.auction.Models.DTO.UserDTOResponse;
import com.interlink.auction.Models.Entities.User;
import com.interlink.auction.Services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true")
@RestController
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public UserDTOResponse login(HttpServletResponse response, @RequestBody LoginDTORequest loginDTORequest) {
        UserDTOResponse user = authenticationService.login(loginDTORequest.getEmail(), loginDTORequest.getPassword());
        if (user != null) {
            Cookie cookie = new Cookie("id", user.getId().toString());
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exits");
        return user;
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("id", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}
