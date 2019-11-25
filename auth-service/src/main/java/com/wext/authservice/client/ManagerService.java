package com.wext.authservice.client;

import com.wext.common.domain.ManagerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", contextId = "managerService")
@RequestMapping("/manager")
public interface ManagerService {

    @PostMapping("/createManager")
    ManagerDTO createManager(@RequestParam String name, @RequestBody String password,@RequestParam String role);

    @GetMapping("/getManagerByName")
    ManagerDTO getManagerByName(@RequestParam String name);

    @GetMapping("/getManagerById")
    ManagerDTO getManagerById(@RequestParam Long id);
}
