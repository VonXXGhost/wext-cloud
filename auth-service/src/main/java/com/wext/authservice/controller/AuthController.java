package com.wext.authservice.controller;

import com.wext.authservice.client.UserService;
import com.wext.authservice.jwt.JwtTokenProvider;
import com.wext.common.domain.BaseResponse;
import com.wext.common.domain.UserDTO;
import com.wext.common.domain.request.AuthenticationRequest;
import com.wext.common.domain.request.UserInfoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    AuthenticationManager authenticationManager;
    JwtTokenProvider jwtTokenProvider;
    UserService userService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationRequest data) {

        try {
            UserDTO user = userService.getUserAutoChoose(data.getUsername());
            String id = String.valueOf(user.getId());

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(id, data.getPassword()));
            String token = jwtTokenProvider.createToken(id, Collections.singletonList("USER"));

            // 构造返回信息
            Map<Object, Object> model = new HashMap<>();
            model.put("login name", data.getUsername());
            model.put("token", token);

            log.info("UserID " + id + " get the token: " + token);
            return ResponseEntity.ok(
                    BaseResponse.successResponse(model)
            );
        } catch (AuthenticationException e) {
            log.info("User " + data.getUsername() + " login failed.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    BaseResponse.failResponse("Invalid username/password supplied")
            );
        }
    }

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody UserInfoRequest data) {

        try {
            UserDTO user = userService.createUser(
                    data.getScreenName(),
                    data.getPassword(),
                    data.getEmail(),
                    data.getNickname()
            );
            log.info("User sign up: " + user);

            // 生成token
            String token = jwtTokenProvider.createToken(String.valueOf(user.getId()), Collections.singletonList("USER"));
            Map<Object, Object> model = new HashMap<>();
            model.put("user", userService.getUserInfo(user));
            model.put("token", token);
            return ResponseEntity.ok(
                    BaseResponse.successResponse(model)
            );

        } catch (NullPointerException e) {
            log.info("User sign up failed: " + data);
            return ResponseEntity.badRequest()
                    .body(BaseResponse.failResponse("Necessary parameters are not satisfied."));
        }
    }

    @RequestMapping(value = "/**",method = RequestMethod.OPTIONS)
    public ResponseEntity handleOptions(){
        return ResponseEntity.noContent().build();
    }
}
