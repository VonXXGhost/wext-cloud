package com.wext.authservice.controller;

import com.wext.authservice.client.ManagerService;
import com.wext.authservice.client.UserService;
import com.wext.authservice.config.RedisKeyPrefixs;
import com.wext.authservice.jwt.JwtTokenProvider;
import com.wext.common.bean.RedisTool;
import com.wext.common.domain.BaseResponse;
import com.wext.common.domain.ManagerDTO;
import com.wext.common.domain.UserDTO;
import com.wext.common.domain.request.AuthenticationRequest;
import com.wext.common.domain.request.UserInfoRequest;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthenticationManager userAuthenticationManager;
    @Autowired
    private AuthenticationManager managerAuthenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private RedisTool<Date> timeRedisTool;

    private static final String USERID_HEADER = "X-data-userID";

    @Value("${wext.keyTTL.tokenMaxTTL}")
    private long tokenMaxTTL;   // 签发的token的最长存活时间

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationRequest data) {

        try {
            UserDTO user = userService.getUserAutoChoose(data.getUsername());
            String id = String.valueOf(user.getId());

            userAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(id, data.getPassword()));
            String token = jwtTokenProvider.createToken(id, Collections.singletonList("USER"));

            // 构造返回信息
            Map<Object, Object> model = new HashMap<>();
            var userInfo = userService.getUserInfo(user);
            model.put("user", userInfo);
            model.put("token", token);

            log.info("UserID " + id + " get the token: " + token);
            return ResponseEntity.ok(
                    BaseResponse.successResponse(model)
            );
        } catch (AuthenticationException e) {
            log.info("User " + data.getUsername() + " login failed.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    BaseResponse.failResponse("Invalid username/password supplied")
            );
        } catch (FeignException e) {
            if (e.status() == 404 && e.getMessage().contains("getUserAutoChoose")) {
                log.info("User " + data.getUsername() + " login failed.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        BaseResponse.failResponse("Invalid username/password supplied")
                );
            } else {
                throw e;
            }
        }
    }

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody UserInfoRequest data) {

        try {
            if (!checkUserInfoRequest(data)) {
                return ResponseEntity.badRequest()
                        .body(BaseResponse.failResponse("Parameter format are wrong."));
            }
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

    @PostMapping("/password")
    public ResponseEntity updatePassword(@RequestHeader(USERID_HEADER) Long id,
                                         @RequestBody UserInfoRequest infoRequest) {
        if (infoRequest == null || infoRequest.getPassword() == null) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.failResponse("Necessary parameters are not satisfied."));
        }

        userService.updateUserPassword(id, infoRequest.getPassword());

        // 更新用户更新时间
        var key = RedisKeyPrefixs.lastPasswordUpdatePrefix + id;
        timeRedisTool.set(key, new Date(), tokenMaxTTL);
        // 返回新token
        String token = jwtTokenProvider.createToken(String.valueOf(id), Collections.singletonList("USER"));
        Map<Object, Object> model = new HashMap<>();
        model.put("user", userService.getUserInfo(id));
        model.put("token", token);
        return ResponseEntity.ok(
                BaseResponse.successResponse(model)
        );
    }

    private boolean checkUserInfoRequest(UserInfoRequest data) {
        return Pattern.matches("^[a-zA-Z]\\w{3,11}$", data.getScreenName()) &&
//                Pattern.matches("[a-zA-Z0-9`~!@#$%^&*()_+\\-={}|\\[\\]\\\\;':\"<>?,./]{6,18}", data.getPassword()) &&
                Pattern.matches("^[!-~]{6,18}$", data.getPassword()) && // 所有键盘的可见字符
                Pattern.matches("^\\S{1,16}$", data.getNickname()) &&
                Pattern.matches("^(.+)@(.+)$", data.getEmail());

    }

    @PostMapping("/manager/login")
    public ResponseEntity managerLogin(@RequestBody AuthenticationRequest data) {

        try {
            ManagerDTO managerDTO = managerService.getManagerByName(data.getUsername());
            String id = String.valueOf(managerDTO.getId());

            managerAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(id, data.getPassword()));
            String token = jwtTokenProvider.createToken(id, Collections.singletonList(managerDTO.getRole()));

            // 构造返回信息
            Map<String, Object> manager = new TreeMap<>();
            manager.put("id", managerDTO.getId());
            manager.put("name", managerDTO.getName());
            manager.put("role", managerDTO.getRole());

            Map<String, Object> model = new TreeMap<>();
            model.put("manager", manager);
            model.put("token", token);

            log.info("Manager ID " + id + " get the token: " + token);
            return ResponseEntity.ok(
                    BaseResponse.successResponse(model)
            );
        } catch (AuthenticationException e) {
            log.info("Manager " + data.getUsername() + " login failed.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    BaseResponse.failResponse("Invalid username/password supplied")
            );
        } catch (FeignException e) {
            if (e.status() == 404 && e.getMessage().contains("getManagerByName")) {
                log.info("Manager " + data.getUsername() + " login failed.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        BaseResponse.failResponse("Invalid username/password supplied")
                );
            } else {
                throw e;
            }
        }
    }

//    @PostMapping("/manager/signup")
//    public ResponseEntity managerSignup(@RequestBody AuthenticationRequest data) {
//
//        return ResponseEntity.ok(
//                managerService.createManager(data.getUsername(), data.getPassword(), "Manager")
//        );
//    }
}
