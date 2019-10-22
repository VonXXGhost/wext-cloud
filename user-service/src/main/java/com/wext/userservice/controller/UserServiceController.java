package com.wext.userservice.controller;

import com.wext.common.domain.UserDTO;
import com.wext.common.utils.CommonTool;
import com.wext.userservice.domain.User;
import com.wext.common.domain.UserInfoItem;
import com.wext.userservice.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserServiceController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUserAutoChoose/{userArg}")
    public UserDTO getUserAutoChoose(@PathVariable String userArg) {
        var user = userService.getUserAutoChoose(userArg);
        return CommonTool.transBean(user, UserDTO.class);
    }

    @PostMapping("/userInfo")
    public UserInfoItem getUserInfo(@RequestBody UserDTO user) {
        var userSrc = new User();
        BeanUtils.copyProperties(user, userSrc);
        return userService.getUserInfo(userSrc);
    }

    @GetMapping("/userInfo/{id}")
    public UserInfoItem getUserInfo(@PathVariable Long id) {
        return userService.getUserInfo(id);
    }

    @GetMapping("/getUserFollowingIds/{id}")
    public List<Long> getUserFollowingIds(@PathVariable Long id, @RequestParam Long page, @RequestParam Integer pageSize) {
        return userService.getUserFollowingIds(id, page, pageSize);
    }

    @GetMapping("/getUserFollowerIds/{id}")
    public List<Long> getUserFollowerIds(@PathVariable Long id, @RequestParam Long page, @RequestParam Integer pageSize) {
        return userService.getUserFollowerIds(id, page, pageSize);
    }

    @GetMapping("/getUserDetail/{userArg}")
    public UserDetails getUserDetail(@PathVariable String userArg) {
        return userService.getUserDetail(userArg);
    }

    @PostMapping("/createUser")
    public UserDTO createUser(@RequestParam String screenName, @RequestBody String password,
                              @RequestParam String email, @RequestParam String nickname) {
        var user = userService.createUser(screenName, password, email, nickname);
        return CommonTool.transBean(user, UserDTO.class);
    }
}
