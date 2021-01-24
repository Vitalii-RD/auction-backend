package com.interlink.auction.Controllers;

import com.interlink.auction.Services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public void getCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("id", "123456789");
        cookie.setPath("/");
        response.addCookie(cookie);
        Cookie[] res = request.getCookies();
        if (res != null) {
            System.out.println(Arrays.stream(res).map(e -> e.getName() + " = " + e.getValue()).collect(Collectors.joining(", ")));
        }
    }
}
