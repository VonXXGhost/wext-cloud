package com.wext.feedservice.service;

import com.wext.common.bean.RedisTool;
import com.wext.common.domain.RepostDTO;
import com.wext.common.domain.UserInfoItem;
import com.wext.common.domain.WextDTO;
import com.wext.common.utils.FeedTool;
import com.wext.common.utils.WextTool;
import com.wext.feedservice.client.UserService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class Pusher {

    private RedisTool<WextDTO> wextRedisTool;
    private RedisTool<String> stringRedisTool;
    private RedisTool<Object> objectRedisTool;
    private UserService userService;

    private static final long feedTTL = 5 * 60; // feed存活时间 30分钟    测试5分钟
    private static final long wextTTL = 10 * 60; // wext存活时间 45分钟    测试10分钟
    private static final long userEntityTTL = wextTTL; // user信息存活时间和wext相等


    private static final String wextKeyPrefix = "wext:wext::";
    private static final String userEntityKeyPrefix = "wext:user:entity::";
    private static final String pathFeedKeyPrefix = "wext:path:feed::";
    private static final String profileFeedKeyPrefix = "wext:user:profile:feed::";
    private static final String userTimelineFeedKeyPrefix = "wext:user:timeline:feed::";


    @Autowired
    public Pusher(RedisTool<WextDTO> wextRedisTool, RedisTool<String> stringRedisTool, RedisTool<Object> objectRedisTool, UserService userService) {
        this.wextRedisTool = wextRedisTool;
        this.stringRedisTool = stringRedisTool;
        this.objectRedisTool = objectRedisTool;
        this.userService = userService;
    }

    @Async
    public void pushWextToPaths(@NonNull WextDTO wext) {
        String fullPath = wext.getFullPath();
        List<String> paths = WextTool.splitPath(fullPath);  // 应该推送的所有节点
        log.info("Push " + wext.getId() + " To:" + paths);
        String feedID = FeedTool.geneFeedID(wext);
        for (String path : paths) {
            String feedKey = pathFeedKeyPrefix + path;
            if (stringRedisTool.hasKey(feedKey)) {  // 未被初始化的不推送
                log.info("Push " + wext.getId() + " To:" + path);
                stringRedisTool.zsetAdd(feedKey, feedID, - FeedTool.getTimestampFromFeedID(feedID).doubleValue());
            }
            Long size = stringRedisTool.zsetSize(feedKey);
            if (size > 600) {  // 数量大于600时裁剪到500
                stringRedisTool.zsetRemove(feedKey, 501, -1);
            }

        }
    }

    @Async
    public void pushDeleteWextToPaths(@NonNull WextDTO wext) {
        String fullPath = wext.getFullPath();
        List<String> paths = WextTool.splitPath(fullPath);  // 应该推送的所有节点
        log.info("Push " + wext.getId() + " To:" + paths);
        String feedID = FeedTool.geneFeedID(wext);
        for (String path : paths) {
            String feedKey = pathFeedKeyPrefix + path;
            if (stringRedisTool.hasKey(feedKey)) {  // 未被初始化的不推送
                stringRedisTool.zsetRemove(feedKey, feedID);
            }

        }
    }

    @Async
    public void updateWext(@NonNull WextDTO wext) {
        String wextKey = wextKeyPrefix + wext.getId();
        wextRedisTool.setIfPresent(wextKey, wext, wextTTL);
        log.info("Wext " + wext.getId() + " updated.");
        log.debug(wext.toString());
    }

    @Async
    public void updateUserInfoItem(@NonNull UserInfoItem userInfoItem) {
        String key = userEntityKeyPrefix + userInfoItem.getId();
        objectRedisTool.setIfPresent(key, userInfoItem, userEntityTTL);
        log.info("UserInfo " + userInfoItem.getId() + " updated.");
        log.debug(userInfoItem.toString());
    }

    @Async
    public void pushToFollowers(@NonNull RepostDTO repost) {
        log.info("Push repost to followers: " + repost.getId());
        String feedID = FeedTool.geneFeedID(repost);
        Long userID = repost.getUserId();
        pushToFollowers(userID, feedID);
    }

    @Async
    public void pushToFollowers(@NonNull WextDTO wext) {
        log.info("Push wext to followers: " + wext.getId());
        String feedID = FeedTool.geneFeedID(wext);
        Long userID = wext.getUserId();
        pushToFollowers(userID, feedID);
    }

    @Async
    public void pushDelete(@NonNull WextDTO wext) {
        log.info("Push wext delete to followers: " + wext.getId());

        String feedID = FeedTool.geneFeedID(wext);
        Long userID = wext.getUserId();

        stringRedisTool.zsetRemove(profileFeedKeyPrefix + userID, feedID);  // 自己的主页feed
        pushDeleteWextToPaths(wext);    // 路径feed
        pushDeleteToFollowers(userID, feedID);  // 粉丝timeline feed
        stringRedisTool.del(wextKeyPrefix + wext.getId());  // wext缓存
    }

    @Async
    public void pushDelete( @NonNull RepostDTO repost) {
        log.info("Push repost delete to followers: " + repost.getId());

        String feedID = FeedTool.geneFeedID(repost);
        Long userID = repost.getUserId();

        stringRedisTool.zsetRemove(profileFeedKeyPrefix + userID, feedID);  // 自己的主页feed
        pushDeleteToFollowers(userID, feedID);  // 粉丝timeline feed
    }

    private void pushToFollowers(@NonNull Long userID, @NonNull String feedID) {
        Long page = 1L;
        Integer pushPageSize = 3000;
        List<Long> followerIds = userService.getUserFollowerIds(userID, page, pushPageSize);
        while (!followerIds.isEmpty()) { // 取得所有关注用户融合后的feedID
            deliveryTimelineReceiver(followerIds, feedID);

            page++;
            followerIds = userService.getUserFollowingIds(userID, page, pushPageSize);
        }
    }

    @Async
    public void deliveryTimelineReceiver(List<Long> userIDs, String feedID) {
        double time = FeedTool.getTimestampFromFeedID(feedID).doubleValue();
        userIDs.forEach(userID -> {
            String key = userTimelineFeedKeyPrefix + userID;
            if (stringRedisTool.hasKey(key)) {  // 只推送给活跃用户
                stringRedisTool.zsetAdd(key, feedID, -time);
                // timeline过大时裁剪
                if (stringRedisTool.zsetSize(key) > 550) {
                    stringRedisTool.zsetRemove(key, 501, -1);
                }
            }
        });
    }

    private void pushDeleteToFollowers(@NonNull Long userID, @NonNull String feedID) {
        Long page = 1L;
        Integer pushPageSize = 3000;
        List<Long> followerIds = userService.getUserFollowerIds(userID, page, pushPageSize);
        while (!followerIds.isEmpty()) { // 取得所有关注用户融合后的feedID
            deliveryTimelineDeleter(followerIds, feedID);

            page++;
            followerIds = userService.getUserFollowingIds(userID, page, pushPageSize);
        }
    }

    @Async
    public void deliveryTimelineDeleter(List<Long> userIDs, String feedID) {
        userIDs.forEach(userID -> {
            String key = userTimelineFeedKeyPrefix + userID;
            if (stringRedisTool.hasKey(key)) {  // 只推送给活跃用户
                stringRedisTool.zsetRemove(key, feedID);
            }
        });
    }

    @Async
    public void pushWext(@NonNull WextDTO wext) {
        if (stringRedisTool.hasKey(profileFeedKeyPrefix + wext.getUserId())) {
            stringRedisTool.zsetAdd(profileFeedKeyPrefix + wext.getUserId(),
                    FeedTool.geneFeedID(wext),
                    -(double) wext.getCreatedTime().getTime()); // 自己的feed
        }
        pushToFollowers(wext);  // follower timeline feed
        pushWextToPaths(wext);  // 路径feed
    }

    @Async
    public void pushRepost(@NonNull RepostDTO repost) {
        if (stringRedisTool.hasKey(profileFeedKeyPrefix + repost.getUserId())) {
            stringRedisTool.zsetAdd(profileFeedKeyPrefix + repost.getUserId(),
                    FeedTool.geneFeedID(repost),
                    -(double) repost.getCreatedTime().getTime()); // 自己的feed
        }
        pushToFollowers(repost);  // follower timeline feed
    }

    @Async
    public void pushNewFollowToTimeline(@NonNull Long followerID, @NonNull Long followID) {
        String followingFeedKey = profileFeedKeyPrefix + followID;
        String followerTimelineKey = userTimelineFeedKeyPrefix + followerID;
        for (String feedID : stringRedisTool.zsetGetByRange(followingFeedKey, 0, 499)) {
            stringRedisTool.zsetAdd(followerTimelineKey, feedID, -(double) FeedTool.getTimestampFromFeedID(feedID));
        }
        if (stringRedisTool.zsetSize(followerTimelineKey) > 550) {
            stringRedisTool.zsetRemove(followerTimelineKey, 501, -1);
        }
    }

    @Async
    public void pushDeleteFollowToTimeline(@NonNull Long followerID, @NonNull Long followID) {
        String followingFeedKey = profileFeedKeyPrefix + followID;
        String followerTimelineKey = userTimelineFeedKeyPrefix + followerID;
        for (String feedID : stringRedisTool.zsetGetByRange(followingFeedKey, 0, 499)) {
            stringRedisTool.zsetRemove(followerTimelineKey, feedID);
        }
    }

}
