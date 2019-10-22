package com.wext.feedservice.controller;

import com.wext.common.domain.TimelineItem;
import com.wext.common.domain.UserInfoItem;
import com.wext.common.domain.WextDTO;
import com.wext.feedservice.service.Puller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PullerController {

    @Autowired
    private Puller puller;

    @GetMapping("/getTimelineItem/{feedID}")
    public TimelineItem getTimelineItem(@PathVariable String feedID) {
        return puller.getTimelineItem(feedID);
    }

    @GetMapping("/pullWext/{wextID}")
    public WextDTO pullWext(@PathVariable String wextID) {
        return puller.pullWext(wextID);
    }

    @GetMapping("/pullUserInfo/{userID}")
    public UserInfoItem pullUserInfo(@PathVariable Long userID) {
        return puller.pullUserInfo(userID);
    }

    @GetMapping("/pullItemsOfPath")
    public List<TimelineItem> pullItemsOfPath(@RequestParam String fullPath,
                                              @RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam String lastWextID) {
        return puller.pullItemsOfPath(fullPath, page, pageSize, lastWextID);
    }

    @GetMapping("/pullUserProfileFeed/{userID}")
    public List<TimelineItem> pullUserProfileFeed(@PathVariable Long userID,
                                              @RequestParam Integer page, @RequestParam Integer pageSize) {
        return puller.pullUserProfileFeed(userID, page, pageSize);
    }

    @GetMapping("/pullTimelineOf/{userID}")
    public List<TimelineItem> pullTimelineOf(@PathVariable Long userID,
                                                  @RequestParam Long page, @RequestParam Integer pageSize, @RequestParam String lastWextID) {
        return puller.pullTimelineOf(userID, page, pageSize, lastWextID);
    }
}
