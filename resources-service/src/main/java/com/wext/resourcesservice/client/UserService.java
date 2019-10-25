package com.wext.resourcesservice.client;

import com.wext.common.domain.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "user-service")
public interface UserService {

    @PostMapping("/updateUserAttr")
    UserDTO updateUserAttr(@RequestParam Long id, @RequestBody Map<String, String> newAttrs);

}
