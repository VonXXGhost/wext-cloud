package com.wext.feedservice.controller;

import com.wext.common.domain.RepostDTO;
import com.wext.common.domain.UserInfoItem;
import com.wext.common.domain.WextDTO;
import com.wext.feedservice.service.Pusher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PusherController {

    @Autowired
    private Pusher pusher;

    @PostMapping("/updateUserInfoItem")
    public ResponseEntity<Void> updateUserInfoItem(@RequestBody UserInfoItem userInfoItem) {
        pusher.updateUserInfoItem(userInfoItem);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pushNewFollowToTimeline")
    public ResponseEntity<Void> pushNewFollowToTimeline(@RequestParam Long followerID, @RequestParam Long followID) {
        pusher.pushNewFollowToTimeline(followerID, followID);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pushDeleteFollowToTimeline")
    public ResponseEntity<Void> pushDeleteFollowToTimeline(@RequestParam Long followerID, @RequestParam Long followID) {
        pusher.pushDeleteFollowToTimeline(followerID, followID);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/pushWext")
    public ResponseEntity<Void> pushWext(@RequestBody WextDTO wext) {
        pusher.pushWext(wext);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/pushRepost")
    public ResponseEntity<Void> pushRepost(@RequestBody RepostDTO repost) {
        pusher.pushRepost(repost);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/updateWext")
    public ResponseEntity<Void> updateWext(@RequestBody WextDTO wext) {
        pusher.updateWext(wext);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/pushDelete/wext")
    public ResponseEntity<Void> pushDelete(@RequestBody WextDTO wext) {
        pusher.pushDelete(wext);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/pushDelete/repost")
    public ResponseEntity<Void> pushDelete(@RequestBody RepostDTO repost) {
        pusher.pushDelete(repost);
        return ResponseEntity.noContent().build();
    }

}
