package com.wext.feedservice.controller;

import com.wext.common.domain.BaseResponse;
import com.wext.common.domain.TimelineItem;
import com.wext.common.domain.UserDTO;
import com.wext.common.domain.WextDTO;
import com.wext.common.utils.FeedTool;
import com.wext.feedservice.client.UserService;
import com.wext.feedservice.client.WextService;
import com.wext.feedservice.service.Puller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/timeline")
@Slf4j
public class TimelineController {

    private Puller puller;
    private UserService userService;
    private WextService wextService;

    private static final String USERID_HEADER = "X-data-userID";

    @Autowired
    public TimelineController(Puller puller, UserService userService, WextService wextService) {
        this.puller = puller;
        this.userService = userService;
        this.wextService = wextService;
    }

    @GetMapping("/path/**")
    public ResponseEntity getTimelineOfPath(HttpServletRequest request,
                                            @RequestParam(required = false, defaultValue = "1") Integer page,
                                            @RequestParam(required = false, defaultValue = "30") Integer pageSize) {
        String fullPath = request.getServletPath().substring(14);
        if (fullPath.equals("")) {
            fullPath = "/"; // 当获取根目录时gateway转发后会自动去掉最后的“/”，判断补回
        }
        long maxCacheSize = 500;
        List<TimelineItem> items;
        if (page * pageSize < maxCacheSize) {
            items = puller.pullItemsOfPath(fullPath, page, pageSize, null);
        } else {
            List<WextDTO> wexts = wextService.getWextsByPrefix(fullPath, page, pageSize);
            items = wexts.stream()
                    .map(FeedTool::geneFeedID)
                    .map(puller::getTimelineItem)
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(
                BaseResponse.successResponse(items)
        );
    }

    @GetMapping("/home")
    public ResponseEntity getHomeTimeline(@RequestHeader(USERID_HEADER) String userID,
                                          @RequestParam(required = false, defaultValue = "1") Long page,
                                          @RequestParam(required = false, defaultValue = "30") Integer pageSize) {
        Long id = Long.parseLong(userID);
        List<TimelineItem> items = puller.pullTimelineOf(id, page, pageSize, null);
        return ResponseEntity.ok(
                BaseResponse.successResponse(items)
        );
    }

    @GetMapping("/user/{username}")
    public ResponseEntity getFeedOfUser(@PathVariable String username,
                                        @RequestParam(required = false, defaultValue = "1") Integer page,
                                        @RequestParam(required = false, defaultValue = "30") Integer pageSize) {
        UserDTO user = userService.getUserAutoChoose(username);
        List<TimelineItem> items = puller.pullUserProfileFeed(user.getId(), page, pageSize);

        return ResponseEntity.ok(
                BaseResponse.successResponse(items)
        );
    }

//    @RequestMapping(value = "/**",method = RequestMethod.OPTIONS)
//    public ResponseEntity handleOptions(){
//        return ResponseEntity.noContent().build();
//    }
}
