package com.wext.userservice.client;

import com.wext.common.domain.UserInfoItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "feed-service", contextId = "puller")
public interface Puller {

    @GetMapping("/pullUserInfo/{userID}")
    public UserInfoItem pullUserInfo(@PathVariable Long userID);
}
