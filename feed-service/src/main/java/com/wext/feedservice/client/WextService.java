package com.wext.feedservice.client;

import com.wext.common.domain.WextDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "wext-service")
public interface WextService {

    @GetMapping("/wextService/getWext/{wextID}")
    public WextDTO getWext(@PathVariable String wextID);

    @GetMapping("/wextService/getWextsByPrefix")
    public List<WextDTO> getWextsByPrefix(@RequestParam String prefix, @RequestParam Integer page, @RequestParam Integer pageSize);

    @GetMapping("/wextService/getWextsOfUser")
    public List<WextDTO> getWextsOfUser(@RequestParam Long userID, @RequestParam Integer page, @RequestParam Integer pageSize);
}
