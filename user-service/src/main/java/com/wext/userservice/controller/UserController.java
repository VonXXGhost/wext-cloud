package com.wext.userservice.controller;

import com.wext.common.domain.BaseResponse;
import com.wext.common.domain.UserInfoItem;
import com.wext.userservice.client.LikeServiceClient;
import com.wext.userservice.client.Puller;
import com.wext.userservice.client.Pusher;
import com.wext.userservice.domain.Follow;
import com.wext.userservice.domain.User;
import com.wext.userservice.domain.UserInfoRequest;
import com.wext.userservice.service.FollowService;
import com.wext.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private UserService userService;
    private FollowService followService;
    private LikeServiceClient likeService;
    private Pusher pusher;
    private Puller puller;

    private static final String USERID_HEADER = "X-data-userID";

    @Autowired
    public UserController(UserService userService, FollowService followService, LikeServiceClient likeService, Pusher pusher, Puller puller) {
        this.userService = userService;
        this.followService = followService;
        this.likeService = likeService;
        this.pusher = pusher;
        this.puller = puller;
    }

    @GetMapping("/me")
    public ResponseEntity currentUser(@RequestHeader(USERID_HEADER) String id) {
        return ResponseEntity.ok(
                BaseResponse.successResponse(puller.pullUserInfo(Long.parseLong(id)))
        );
    }

    @PostMapping("/me")
    public ResponseEntity updateMyInfo(@RequestHeader(USERID_HEADER) String id,
                                       @RequestBody UserInfoRequest body) {
        Map<String, String> attrs = new HashMap<>();

        if (body.getNickname() != null) {
            attrs.put("nickname", body.getNickname());
        }
        if (body.getProfile() != null) {
            attrs.put("profile", body.getProfile());
        }

        User user = userService.updateUserAttr(Long.parseLong(id), attrs);
        log.info(id + " update info: " + attrs);
        // 更新缓存
        UserInfoItem item = userService.getUserInfo(user);
        pusher.updateUserInfoItem(item);

        return ResponseEntity.ok(
                BaseResponse.successResponse(item)
        );

    }

    @GetMapping("/{username}/info")
    public ResponseEntity userInfoByNameOrId(@PathVariable String username,
                                             @RequestHeader(value = USERID_HEADER, required = false) String uid) {
        User user = userService.getUserAutoChoose(username);        // 获取目标用户
        Map<String, Object> resp = new LinkedHashMap<>();   // 基础信息
        resp.put("info", puller.pullUserInfo(user.getId()));

        if (uid != null) {      // 已登录用户获得信息
            resp.put("relationship", followService.getRelationshipBetween(Long.parseLong(uid), user.getId()));
        }

        return ResponseEntity.ok(
                BaseResponse.successResponse(resp)
        );
    }

    @PutMapping("/{username}/follow")
    public ResponseEntity followUser(@PathVariable String username,
                                     @RequestHeader(USERID_HEADER) String id) {
        User user = userService.getUserAutoChoose(username);
        Follow follow = followService.createFollow(Long.parseLong(id), user.getId());
        log.info("User " + id + "success follow " + user.getId());

        // 更新缓存,follow类操作双方都要更新
        pusher.updateUserInfoItem(userService.getUserInfo(user));
        pusher.updateUserInfoItem(userService.getUserInfo(Long.parseLong(id)));
        // 更新feed
        pusher.pushNewFollowToTimeline(Long.parseLong(id), user.getId());

        return ResponseEntity.ok(BaseResponse.successResponse(follow));
    }

    @DeleteMapping("/{username}/follow")
    public ResponseEntity unfollowUser(@PathVariable String username,
                                       @RequestHeader(USERID_HEADER) String id) {
        User user = userService.getUserAutoChoose(username);
        followService.destroyFollow(Long.parseLong(id), user.getId());
        log.info("User " + id + "success unfollow " + user.getId());

        // 更新缓存,follow类操作双方都要更新
        pusher.updateUserInfoItem(userService.getUserInfo(user));
        pusher.updateUserInfoItem(userService.getUserInfo(Long.parseLong(id)));
        // 更新feed
        pusher.pushDeleteFollowToTimeline(Long.parseLong(id), user.getId());

        return ResponseEntity.ok(
                BaseResponse.successResponse(null)
        );
    }

    @GetMapping("/{username}/following")
    public ResponseEntity userFollowing(@PathVariable String username,
                                        @RequestParam(required = false, defaultValue = "1") Long page,
                                        @RequestParam(required = false, defaultValue = "30") Integer pageSize) {
        if (pageSize > 100) // 单页限制
            return ResponseEntity.status(403).body(BaseResponse.failResponse("Page size is too big."));

        Long id = userService.getUserAutoChoose(username).getId();
        List<Long> userIds = userService.getUserFollowingIds(id, page, pageSize);
        List followings = userIds.stream()
                .map(x -> puller.pullUserInfo(x))
                .collect(Collectors.toList());

        log.debug(followings.toString());
        return ResponseEntity.ok(BaseResponse.successResponse(followings));
    }

    @GetMapping("/{username}/followers")
    public ResponseEntity userFollowers(@PathVariable String username,
                                        @RequestParam(required = false, defaultValue = "1") Long page,
                                        @RequestParam(required = false, defaultValue = "30") Integer pageSize) {
        if (pageSize > 100) // 单页限制
            return ResponseEntity.status(403).body(BaseResponse.failResponse("Page size is too big."));

        Long id = userService.getUserAutoChoose(username).getId();
        List<Long> userIds = userService.getUserFollowerIds(id, page, pageSize);

        List followers = userIds.stream()
                .map(x -> puller.pullUserInfo(x))
                .collect(Collectors.toList());

        log.debug(followers.toString());
        return ResponseEntity.ok(BaseResponse.successResponse(followers));
    }

    @GetMapping("/{username}/likes")
    public ResponseEntity userLikes(@PathVariable String username,
                                    @RequestParam(required = false, defaultValue = "1") Integer page,
                                    @RequestParam(required = false, defaultValue = "30") Integer pageSize) {
        User user = userService.getUserAutoChoose(username);
        Map<String, Object> likes = likeService.getLikesOfUser(user.getId(), page, pageSize);
        return ResponseEntity.ok(
                BaseResponse.successResponse(likes)
        );
    }

//    @RequestMapping(value = "/**",method = RequestMethod.OPTIONS)
//    public ResponseEntity handleOptions(){
//        return ResponseEntity.noContent().build();
//    }

}
