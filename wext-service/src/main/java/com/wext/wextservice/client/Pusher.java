package com.wext.wextservice.client;

import com.wext.common.domain.RepostDTO;
import com.wext.common.domain.UserInfoItem;
import com.wext.common.domain.WextDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "feed-service", contextId = "pusher")
public interface Pusher {
    @PostMapping("/updateUserInfoItem")
    ResponseEntity<Void> updateUserInfoItem(@RequestBody UserInfoItem userInfoItem);

    @GetMapping("/pushNewFollowToTimeline")
    ResponseEntity<Void> pushNewFollowToTimeline(@RequestParam Long followerID, @RequestParam Long followID);

    @GetMapping("/pushDeleteFollowToTimeline")
    ResponseEntity<Void> pushDeleteFollowToTimeline(@RequestParam Long followerID, @RequestParam Long followID);

    @PostMapping("/pushWext")
    ResponseEntity<Void> pushWext(@RequestBody WextDTO wext);

    @PostMapping("/pushRepost")
    ResponseEntity<Void> pushRepost(@RequestBody RepostDTO repost);

    @PostMapping("/updateWext")
    ResponseEntity<Void> updateWext(@RequestBody WextDTO wext);

    @PostMapping("/pushDelete/wext")
    ResponseEntity<Void> pushDelete(@RequestBody WextDTO wext);

    @PostMapping("/pushDelete/repost")
    ResponseEntity<Void> pushDelete(@RequestBody RepostDTO repost);
}
