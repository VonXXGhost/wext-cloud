package com.wext.wextservice.controller;

import com.wext.common.domain.WextDTO;
import com.wext.common.utils.CommonTool;
import com.wext.wextservice.domain.Wext;
import com.wext.wextservice.service.WextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wextService")
public class WextServiceController {

    @Autowired
    private WextService wextService;

    @GetMapping("/getWext/{wextID}")
    public WextDTO getWext(@PathVariable String wextID) {
        Wext wext = wextService.getWext(wextID);
        return CommonTool.transBean(wext, WextDTO.class);
    }

    @GetMapping("/getWextsByPrefix")
    public List<WextDTO> getWextsByPrefix(@RequestParam String prefix, @RequestParam Integer page, @RequestParam Integer pageSize) {
        List<Wext> wexts = wextService.getWextsByPrefix(prefix, page, pageSize);
        return CommonTool.transBean(wexts, WextDTO.class);
    }

    @GetMapping("/getWextsOfUser")
    public List<WextDTO> getWextsOfUser(@RequestParam Long userID, @RequestParam Integer page, @RequestParam Integer pageSize) {
        List<Wext> wexts = wextService.getWextsOfUser(userID, page, pageSize);
        return CommonTool.transBean(wexts, WextDTO.class);
    }

}
