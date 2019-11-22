package com.wext.wextservice.controller;

import com.wext.common.domain.RepostDTO;
import com.wext.common.utils.CommonTool;
import com.wext.wextservice.domain.Repost;
import com.wext.wextservice.service.RepostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repostService")
public class RepostServiceController {
    @Autowired
    private RepostService repostService;

    @GetMapping("/getRepostsFromUser/{userID}")
    public List<RepostDTO> getRepostsFromUser(@PathVariable Long userID, @RequestParam Integer page, @RequestParam Integer pageSize) {
        var reposts = (List<Repost>) repostService.getRepostsFromUser(userID, page, pageSize).get("reposts");
        return CommonTool.transBean(reposts, RepostDTO.class);
    }

}
