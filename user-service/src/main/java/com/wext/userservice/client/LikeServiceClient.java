package com.wext.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "wext-service", contextId = "like-service")
public interface LikeServiceClient {

    @GetMapping("/likeService/getLikesOfUser/{userID}")
    Map<String, Object> getLikesOfUser(@PathVariable Long userID, @RequestParam Integer page, @RequestParam Integer pageSize);

}
