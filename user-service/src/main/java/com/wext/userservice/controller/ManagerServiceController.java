package com.wext.userservice.controller;

import com.wext.common.domain.ManagerDTO;
import com.wext.common.utils.CommonTool;
import com.wext.userservice.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manager")
public class ManagerServiceController {
    @Autowired
    private ManagerService managerService;

    @PostMapping("/createManager")
    public ManagerDTO createManager(@RequestParam String name, @RequestBody String password, @RequestParam String role) {
        var manager = managerService.createManager(name, password, role);
        return CommonTool.transBean(manager, ManagerDTO.class);
    }

    @GetMapping("/getManagerByName")
    public ManagerDTO getManagerByName(@RequestParam String name) {
        var manager = managerService.getManagerByName(name);
        return CommonTool.transBean(manager, ManagerDTO.class);
    }

    @GetMapping("/getManagerById")
    public ManagerDTO getManagerById(@RequestParam Long id) {
        var manager = managerService.getManagerById(id);
        return CommonTool.transBean(manager, ManagerDTO.class);
    }
}
