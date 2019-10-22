package com.wext.wextservice.controller;

import com.wext.wextservice.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/likeService")
public class LikeServiceController {

    @Autowired
    private LikeService likeService;

    @GetMapping("/getLikesOfUser/{userID}")
    public Map<String, Object> getLikesOfUser(@PathVariable Long userID, @RequestParam Integer page, @RequestParam Integer pageSize) {
        return likeService.getLikesOfUser(userID, page, pageSize);
    }

}
