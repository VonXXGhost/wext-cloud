package com.wext.authservice.client;

import com.wext.common.domain.UserDTO;
import com.wext.common.domain.UserInfoItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service")
public interface UserService {

    @GetMapping("/getUserAutoChoose/{userArg}")
    UserDTO getUserAutoChoose(@PathVariable String userArg);

    @PostMapping("/createUser")
    UserDTO createUser(@RequestParam String screenName, @RequestBody String password,
                       @RequestParam String email, @RequestParam String nickname);

    @PostMapping("/userInfo")
    UserInfoItem getUserInfo(@RequestBody UserDTO user);

    @GetMapping("/userInfo/{id}")
    UserInfoItem getUserInfo(@PathVariable Long id);

    @PostMapping("/updateUserPassword")
    UserDTO updateUserPassword(@RequestParam Long id, @RequestBody String password);
}
