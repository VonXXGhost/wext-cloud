package com.wext.feedservice.client;

import com.wext.common.domain.UserDTO;
import com.wext.common.domain.UserInfoItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@FeignClient(name = "user-service")
public interface UserService {
    @GetMapping("/getUserAutoChoose/{userArg}")
    UserDTO getUserAutoChoose(@PathVariable("userArg") String userArg);

    @PostMapping("/userInfo")
    UserInfoItem getUserInfo(@RequestBody UserDTO user);

    @GetMapping("/userInfo/{id}")
    UserInfoItem getUserInfo(@PathVariable Long id);

    @GetMapping("/getUserFollowingIds/{id}")
    List<Long> getUserFollowingIds(@PathVariable("id") Long id, @RequestParam Long page, @RequestParam Integer pageSize);

    @GetMapping("/getUserFollowerIds/{id}")
    List<Long> getUserFollowerIds(@PathVariable("id") Long id, @RequestParam Long page, @RequestParam Integer pageSize);

    @PostMapping("/getUserInfosByIds")
    Map<Long, UserInfoItem> getUserInfosByIds(@RequestBody Collection<Long> ids);
}
