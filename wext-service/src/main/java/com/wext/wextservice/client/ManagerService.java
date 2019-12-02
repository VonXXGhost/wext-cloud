package com.wext.wextservice.client;

import com.wext.common.domain.ManagerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", contextId = "managerService")
@RequestMapping("/manager")
public interface ManagerService {

    @GetMapping("/getManagerById")
    ManagerDTO getManagerById(@RequestParam Long id);

}
