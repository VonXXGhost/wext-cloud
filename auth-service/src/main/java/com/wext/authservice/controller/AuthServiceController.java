package com.wext.authservice.controller;

import com.wext.authservice.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthServiceController {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @GetMapping("/getUserIDFromToken")
    public String getUSerIDFromToken(@RequestParam String bearerToken) {
        var token = jwtTokenProvider.resolveToken(bearerToken);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            return jwtTokenProvider.getUsername(token);
        }
        return null;
    }
}