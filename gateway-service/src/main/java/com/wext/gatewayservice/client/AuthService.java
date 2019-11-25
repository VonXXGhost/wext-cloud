package com.wext.gatewayservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service")
public interface AuthService {

    @GetMapping("/getUserIDFromToken")
    String getUSerIDFromToken(@RequestParam String bearerToken);

    @GetMapping("/getManagerIDFromToken")
    String getManagerIDFromToken(@RequestParam String bearerToken);
}
