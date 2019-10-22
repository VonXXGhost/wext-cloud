package com.wext.wextservice.client;

import com.wext.common.domain.TimelineItem;
import com.wext.common.domain.UserInfoItem;
import com.wext.common.domain.WextDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "feed-service", contextId = "puller")
public interface Puller {

    @GetMapping("/getTimelineItem/{feedID}")
    TimelineItem getTimelineItem(@PathVariable String feedID);

    @GetMapping("/pullWext/{wextID}")
    WextDTO pullWext(@PathVariable String wextID);

    @GetMapping("/pullUserInfo/{userID}")
    UserInfoItem pullUserInfo(@PathVariable Long userID);

    @GetMapping("/pullItemsOfPath")
    List<TimelineItem> pullItemsOfPath(@RequestParam String fullPath,
                                       @RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam String lastWextID);

    @GetMapping("/pullUserProfileFeed/{userID}")
    List<TimelineItem> pullUserProfileFeed(@PathVariable Long userID,
                                           @RequestParam Integer page, @RequestParam Integer pageSize);

    @GetMapping("/pullTimelineOf/{userID}")
    List<TimelineItem> pullTimelineOf(@PathVariable Long userID,
                                      @RequestParam Long page, @RequestParam Integer pageSize, @RequestParam String lastWextID);
}
