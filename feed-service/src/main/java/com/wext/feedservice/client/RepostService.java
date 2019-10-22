package com.wext.feedservice.client;

import com.wext.common.domain.RepostDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "wext-service", contextId = "repostService")
public interface RepostService {

    @GetMapping("/repostService/getRepostsFromUser/{userID}")
    List<RepostDTO> getRepostsFromUser(@PathVariable Long userID, @RequestParam Integer page, @RequestParam Integer pageSize);

}