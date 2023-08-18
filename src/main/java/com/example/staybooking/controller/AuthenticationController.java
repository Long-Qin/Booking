package com.example.staybooking.controller;

import com.example.staybooking.model.Token;
import com.example.staybooking.model.User;
import com.example.staybooking.model.UserRole;
import com.example.staybooking.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/authenticate/guest")
    public Token authenticateGuest(@RequestBody User user) {
        return authenticationService.authenticate(user, UserRole.ROLE_GUEST);
    }

    @PostMapping("/authenticate/host")
    public Token authenticate(@RequestBody User user) {
        return authenticationService.authenticate(user, UserRole.ROLE_HOST);
    }
}
