package com.wext.feedservice.client;

import com.wext.common.domain.WextDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@FeignClient(name = "wext-service")
public interface WextService {

    @GetMapping("/wextService/getWext/{wextID}")
    WextDTO getWext(@PathVariable String wextID);

    @GetMapping("/wextService/getWextsByPrefix")
    List<WextDTO> getWextsByPrefix(@RequestParam String prefix, @RequestParam Integer page, @RequestParam Integer pageSize);

    @GetMapping("/wextService/getWextsOfUser")
    List<WextDTO> getWextsOfUser(@RequestParam Long userID, @RequestParam Integer page, @RequestParam Integer pageSize);

    @PostMapping("/wextService/getWexts")
    Map<String, WextDTO> getWexts(@RequestBody Collection<String> wextIDs);
}
