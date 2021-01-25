package com.interlink.auction.Controllers;

import com.interlink.auction.Models.DTO.LoginDTORequest;
import com.interlink.auction.Services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public void login(HttpServletResponse response, @RequestBody LoginDTORequest loginDTORequest) {
        authenticationService.login(response, loginDTORequest);
    }
}
