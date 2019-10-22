package com.wext.feedservice.client;

import com.wext.common.domain.UserDTO;
import com.wext.common.domain.UserInfoItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserService {
    @GetMapping("/getUserAutoChoose/{userArg}")
    public UserDTO getUserAutoChoose(@PathVariable("userArg") String userArg);

    @PostMapping("/userInfo")
    public UserInfoItem getUserInfo(@RequestBody UserDTO user);

    @GetMapping("/userInfo/{id}")
    public UserInfoItem getUserInfo(@PathVariable Long id);

    @GetMapping("/getUserFollowingIds/{id}")
    public List<Long> getUserFollowingIds(@PathVariable("id") Long id, @RequestParam Long page, @RequestParam Integer pageSize);

    @GetMapping("/getUserFollowerIds/{id}")
    public List<Long> getUserFollowerIds(@PathVariable("id") Long id, @RequestParam Long page, @RequestParam Integer pageSize);
}
