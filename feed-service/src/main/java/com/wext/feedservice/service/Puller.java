package com.wext.feedservice.service;

import com.wext.common.bean.RedisTool;
import com.wext.common.domain.RepostDTO;
import com.wext.common.domain.TimelineItem;
import com.wext.common.domain.UserInfoItem;
import com.wext.common.domain.WextDTO;
import com.wext.common.utils.FeedTool;
import com.wext.common.utils.WextTool;
import com.wext.feedservice.client.RepostService;
import com.wext.feedservice.client.UserService;
import com.wext.feedservice.client.WextService;
import com.wext.feedservice.config.RedisKeyPrefixs;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Puller {

    private WextService wextService;
    private UserService userService;
    private RepostService repostService;
    private RedisTool<WextDTO> wextRedisTool;
    private RedisTool<String> stringRedisTool;
    private RedisTool<Object> objectRedisTool;

    @Value("${wext.keyTTL.pathFeed}")
    private long pathFeedTTL; // path// feed存活时间

    @Value("${wext.keyTTL.wextItem}")
    private long wextTTL; // wext存活时间

    @Value("${wext.keyTTL.userEntity}")
    private long userEntityTTL; // user信息存活时间

    @Value("${wext.keyTTL.timelineFeed}")
    private long timelineFeedTTl; // timeline feed存活时间

    private static final int defaultPageSize = 100;

    private Queue<Long> userIDBuffer = new LinkedBlockingDeque<>();  // 待读取sql写入redis的用户id
    private Queue<String> wextIDBuffer = new LinkedBlockingDeque<>();  // 待读取sql写入redis的wext id

    @Autowired
    public Puller(WextService wextService, UserService userService, RepostService repostService, RedisTool<WextDTO> wextRedisTool, RedisTool<String> stringRedisTool, RedisTool<Object> objectRedisTool) {
        this.wextService = wextService;
        this.userService = userService;
        this.repostService = repostService;
        this.wextRedisTool = wextRedisTool;
        this.stringRedisTool = stringRedisTool;
        this.objectRedisTool = objectRedisTool;
    }

    /**
     * 拉取wext
     */
    public WextDTO pullWext(@NonNull String wextID) {
        String wextKey = RedisKeyPrefixs.wextKeyPrefix + wextID;
        WextDTO wext;
        if (wextRedisTool.hasKey(wextKey)) {
            // 存在时直接读取
            wext = wextRedisTool.get(wextKey);
            wextRedisTool.expire(wextKey, wextTTL); // 更新缓存时间
        } else {
            // 不存在时建立
            wext = wextService.getWext(wextID);
            wext.setPicList(WextTool.stringToPicsList(wext.getPics())); // string类型图片存储属性会被忽略，在存入redis前就要转换
            wextRedisTool.set(wextKey, wext, wextTTL);
        }
        log.debug(wext.toString());
        return wext;
    }

    private WextDTO pullWext(@NonNull String wextID, boolean fastModel) {
        if (fastModel) {
            String wextKey = RedisKeyPrefixs.wextKeyPrefix + wextID;
            WextDTO wext = wextRedisTool.get(wextKey);
            if (wext == null) {
                // 不存在时放入缓存队列
                wextIDBuffer.offer(wextID);
            } else {
                wextRedisTool.expire(wextKey, wextTTL); // 更新缓存时间
            }
            return wext;
        } else {
            return pullWext(wextID);
        }
    }

    private Map<String, WextDTO> flushWextIDs() {
        var wids = new LinkedList<String>();
        while (!wextIDBuffer.isEmpty()) {   // 拷贝并清空buffer，预防可能的线程不安全情况发生
            wids.add(wextIDBuffer.poll());
        }
        var wexts = wextService.getWexts(wids);
        wexts.forEach((id, wext) -> {   // 存入到redis中
            String wextKey = RedisKeyPrefixs.wextKeyPrefix + id;
            wext.setPicList(WextTool.stringToPicsList(wext.getPics())); // string类型图片存储属性会被忽略，在存入redis前就要转换
            wextRedisTool.set(wextKey, wext, wextTTL);
        });
        return wexts;
    }

    /**
     * 拉取用户综合信息
     */
    public UserInfoItem pullUserInfo(@NonNull Long userID) {
        String userKey = RedisKeyPrefixs.userEntityKeyPrefix + userID;
        UserInfoItem user = (UserInfoItem) objectRedisTool.get(userKey);
        if (user == null) {
            // 不存在时的情况，建立
            user = userService.getUserInfo(userID);
            objectRedisTool.set(userKey, user, userEntityTTL);
        } else {
            objectRedisTool.expire(userKey, userEntityTTL); // 更新缓存时间
        }
        log.debug(user.toString());
        return user;
    }

    /**
     * 拉取用户综合信息
     *
     * @param fastModel: 开启后如果未击中缓存，直接返回null
     */
    private UserInfoItem pullUserInfo(@NonNull Long userID, boolean fastModel) {
        if (fastModel) {
            String userKey = RedisKeyPrefixs.userEntityKeyPrefix + userID;
            UserInfoItem user = (UserInfoItem) objectRedisTool.get(userKey);
            if (user == null) {
                // 不存在时放入缓存队列
                userIDBuffer.offer(userID);
            } else {
                objectRedisTool.expire(userKey, userEntityTTL); // 更新缓存时间
            }
            return user;
        } else {
            return pullUserInfo(userID);
        }
    }

    private Map<Long, UserInfoItem> flushUserIDs() {
        var uids = new LinkedList<Long>();
        while (!userIDBuffer.isEmpty()) {   // 拷贝并清空buffer，预防可能的线程不安全情况发生
            uids.add(userIDBuffer.poll());
        }
        var userInfos = userService.getUserInfosByIds(uids);
        userInfos.forEach((id, info) -> {   // 存入到redis中
            String userKey = RedisKeyPrefixs.userEntityKeyPrefix + id;
            objectRedisTool.set(userKey, info, userEntityTTL);
        });
        return userInfos;
    }

    /**
     * 拉取节点下的最新wext
     */
    public List<TimelineItem> pullItemsOfPath(@NonNull String fullPath,
                                              Integer page, Integer pageSize, String lastWextID) {
        // 最多缓存500条
        String feedKey = RedisKeyPrefixs.pathFeedKeyPrefix + fullPath;
        List<String> feedIDs;
        log.debug(feedKey);
        if (stringRedisTool.hasKey(feedKey)) {
            // 从redis读取feed
            feedIDs = new ArrayList<>(stringRedisTool.zsetGetByRange(feedKey, 0, 499));
            log.debug(feedIDs.toString());
        } else {
            // 建立redis feed缓存
            feedIDs = wextService.getWextsByPrefix(fullPath, 1, 500).stream()
                    .map(FeedTool::geneFeedID)   // feedID生成
                    .collect(Collectors.toList());  // 包含子节点
            Set<ZSetOperations.TypedTuple<String>> feedTupSets = feedIDs.stream()
                    .map(feedID ->
                            new DefaultTypedTuple<String>(feedID,
                                    -FeedTool.getTimestampFromFeedID(feedID).doubleValue()))  // 放入到redis中，score为负数
                    .collect(Collectors.toSet());
            stringRedisTool.zsetAdd(feedKey, feedTupSets);
//            feedIDs.forEach(feedID ->
//                    stringRedisTool.zsetAdd(feedKey, feedID, - FeedTool.getTimestampFromFeedID(feedID).doubleValue()));
        }
        stringRedisTool.expire(feedKey, pathFeedTTL); // 更新feed缓存时间

        // 截取分析
        List<String> resultFeedIDs;
        if (pageSize == null || pageSize < 1) {
            pageSize = defaultPageSize;
        }
        if (lastWextID != null) {   // 优先使用id参数
            resultFeedIDs = FeedTool.feedIDFilter(feedIDs, lastWextID);
            resultFeedIDs = resultFeedIDs.subList(0, pageSize);
            log.debug(resultFeedIDs.toString());
        } else {
            if (page == null || page < 1) {
                page = 1;
            }

            if (feedIDs.size() >= page * pageSize) {   // 边界处理
                resultFeedIDs = feedIDs.subList((page - 1) * pageSize, page * pageSize);
            } else if (feedIDs.size() >= (page - 1) * pageSize) {
                resultFeedIDs = feedIDs.subList((page - 1) * pageSize, feedIDs.size());
            } else {
                resultFeedIDs = new ArrayList<>();
            }

            log.debug(resultFeedIDs.toString());
        }

        // 获取对应wext
//        List<Wext> wexts = resultFeedIDs.stream()
//                .map(this::pullWext).collect(Collectors.toList());  // pullWext中已含有时间更新
//        log.debug(wexts.toString());

        // 获取对应user并转换为TimelineItem
//        List<TimelineItem> items = resultFeedIDs.stream()
//                .map(this::getTimelineItem)
//                .collect(Collectors.toList());
        var items = getTimelineItems(resultFeedIDs);
        log.debug(items.toString());
        return items;
    }

    /**
     * 拉取用户wext、转发的个人feed流
     */
    public List<TimelineItem> pullUserProfileFeed(@NonNull Long userID, Integer page, Integer pageSize) {
        // 最多缓存500条
        String profileFeedKey = RedisKeyPrefixs.profileFeedKeyPrefix + userID;
        List<String> feedIDs;
        List<TimelineItem> timelineItems;

        try {
            if (stringRedisTool.hasKey(profileFeedKey)) {
                // 存在直接读取
                feedIDs = new ArrayList<>(stringRedisTool.zsetGetByRange(profileFeedKey, 0, 499));
    //            timelineItems = feedIDs.stream()
    //                    .map(this::getTimelineItem)
    //                    .collect(Collectors.toList());
                timelineItems = getTimelineItems(feedIDs.subList(
                        (page - 1) * pageSize,
                        Math.min(page * pageSize, feedIDs.size())
                ));
                log.debug(timelineItems.toString());
            } else {
                // 不存在，建立
                List<WextDTO> wexts = wextService.getWextsOfUser(userID, 1, 500);
                List<RepostDTO> reposts = repostService.getRepostsFromUser(userID, 1, 500);
                timelineItems = mergeWextAndRepost(wexts, reposts, 500);
                // 转化为对应格式存储到redis中
                transAndAddToRedisFeed(profileFeedKey, timelineItems);
                timelineItems = timelineItems.subList(
                        (page - 1) * pageSize,
                        Math.min(page * pageSize, timelineItems.size())
                );
            }
            stringRedisTool.expire(profileFeedKey, pathFeedTTL);    // 更新缓存时间
            return timelineItems;
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }

    private void transAndAddToRedisFeed(String profileFeedKey, List<TimelineItem> timelineItems) {
        List<String> feedIDs;
        feedIDs = timelineItems.stream()
                .map(i -> {
                    if (i.isRepost()) {
                        return FeedTool.geneFeedID(i.getWext(), i.getRepostUser().getId(), i.getCreatedTime().getTime());
                    } else {
                        return FeedTool.geneFeedID(i.getWext());
                    }
                }).collect(Collectors.toList());
        feedIDs.forEach(feedID ->
                stringRedisTool.zsetAdd(profileFeedKey, feedID, -FeedTool.getTimestampFromFeedID(feedID).doubleValue()));   // 放入到redis中，score为负数
    }

    private List<String> pullUserProfileFeedIDs(@NonNull Long userID) {
        // 最多缓存500条
        String profileFeedKey = RedisKeyPrefixs.profileFeedKeyPrefix + userID;
        List<String> feedIDs;

        if (stringRedisTool.hasKey(profileFeedKey)) {
            // 存在直接读取
            feedIDs = new ArrayList<>(stringRedisTool.zsetGetByRange(profileFeedKey, 0, 499));
        } else {
            // 不存在，建立
            List<WextDTO> wexts = wextService.getWextsOfUser(userID, 1, 500);
            List<RepostDTO> reposts = repostService.getRepostsFromUser(userID, 1, 500);
            List<TimelineItem> timelineItems = mergeWextAndRepost(wexts, reposts, 100);

            // 转化为对应格式存储到redis中
            feedIDs = timelineItems.stream()
                    .map(i -> {
                        if (i.isRepost()) {
                            return FeedTool.geneFeedID(i.getWext(), i.getRepostUser().getId(), i.getCreatedTime().getTime());
                        } else {
                            return FeedTool.geneFeedID(i.getWext());
                        }
                    }).collect(Collectors.toList());
            feedIDs.forEach(feedID ->
                    stringRedisTool.zsetAdd(profileFeedKey, feedID, -FeedTool.getTimestampFromFeedID(feedID).doubleValue()));   // 放入到redis中，score为负数
        }
        stringRedisTool.expire(profileFeedKey, pathFeedTTL);    // 更新缓存时间
        return feedIDs;
    }

    public TimelineItem getTimelineItem(@NonNull String feedID) {
        /*
         * 存储规则：W|R : wextID : (userID) : time
         * 以冒号作分隔号，W为原创R为转发
         * 只有转发时有userID参数，对应转发者的id
         */
        String wextID = FeedTool.getIDFromFeedID(feedID);
        WextDTO wext = pullWext(wextID);
        UserInfoItem userInfoItem = pullUserInfo(wext.getUserId());
        if (feedID.charAt(0) == 'W') {  // 原创
            return new TimelineItem(wext, false, userInfoItem, null, null);
        } else {    // 转发
            UserInfoItem repostUser = pullUserInfo(Long.parseLong(feedID.split(":")[2]));
            Date time = new Date();
            time.setTime(Long.parseLong(feedID.split(":")[3]));
            return new TimelineItem(wext, true, userInfoItem, repostUser, time);
        }
    }

    private List<TimelineItem> getTimelineItems(@NonNull List<String> feedIDs) {
        /*
         * 存储规则：W|R : wextID : (userID) : time
         * 以冒号作分隔号，W为原创R为转发
         * 只有转发时有userID参数，对应转发者的id
         */
        List<TimelineItem> timelineItems = new ArrayList<>(feedIDs.size());
        var wexts = new ArrayList<WextDTO>(feedIDs.size());
        feedIDs.stream()
                .map(FeedTool::getIDFromFeedID)
                .forEach(wid -> wexts.add(pullWext(wid, true))); // 存入已有缓存数据到中继表
        var flushWexts = flushWextIDs();  // 刷新存入未缓存数据
        for (int i = 0; i < wexts.size(); i++) {
            if (wexts.get(i) == null) {
                var wextID = FeedTool.getIDFromFeedID(feedIDs.get(i));
                wexts.set(i, flushWexts.getOrDefault(wextID, pullWext(wextID)));
            }
        }

        var userIDs = wexts.stream()    // 提取user id打包获得user info
                .map(WextDTO::getUserId)
                .collect(Collectors.toSet());
        feedIDs.stream()    // 提取转发用户
                .filter(feedID -> feedID.charAt(0) == 'R')
                .map(feedID -> Long.parseLong(feedID.split(":")[2]))
                .forEach(userIDs::add);
        var userInfos = new HashMap<Long, UserInfoItem>(userIDs.size());
        userIDs.stream()
                .map(id -> pullUserInfo(id, true))
                .filter(Objects::nonNull)
                .forEach(userInfoItem -> userInfos.put(userInfoItem.getId(), userInfoItem));    // 先存入已缓存的数据
        userInfos.putAll(flushUserIDs());   // 刷新存入未缓存的数据

        for (int i = 0; i < feedIDs.size(); i++) {
            var feedID = feedIDs.get(i);
            var wext = wexts.get(i);
            var userInfoItem = userInfos.getOrDefault(
                    wext.getUserId(), pullUserInfo(wext.getUserId())    // 考虑可能失败的情况时要重新获取
            );
            if (feedID.charAt(0) == 'W') {  // 原创
                timelineItems.add(
                        new TimelineItem(wext, false, userInfoItem, null, null)
                );
            } else {    // 转发
                var ruid = Long.parseLong(feedID.split(":")[2]);
                UserInfoItem repostUser = userInfos.getOrDefault(ruid, pullUserInfo(ruid));
                Date time = new Date();
                time.setTime(Long.parseLong(feedID.split(":")[3]));
                timelineItems.add(
                        new TimelineItem(wext, true, userInfoItem, repostUser, time)
                );
            }
        }
        return timelineItems;
    }

    private List<TimelineItem> mergeWextAndRepost(@NonNull List<WextDTO> wexts, @NonNull List<RepostDTO> reposts, int maxSize) {
        // 归并wext和repost为TimelineItem
        int wp = 0; // wexts指针
        int rp = 0; // reposts指针
        List<TimelineItem> items = new ArrayList<>();   // 结果

        while (items.size() < maxSize) {
            if (wp == wexts.size()) {   // wexts到头，填补reposts剩余部分
                items.addAll(
                        reposts.subList(rp, reposts.size()).stream()
                                .map(this::repostToTimeline).collect(Collectors.toList())
                );
                break;
            }
            if (rp == reposts.size()) { // reposts到头，填补wexts剩余部分
                items.addAll(
                        wexts.subList(wp, wexts.size()).stream()
                                .map(this::wextToTimeline).collect(Collectors.toList())
                );
                break;
            }
            if (wexts.get(wp).getCreatedTime().after(reposts.get(rp).getCreatedTime())) {   // 当前所指wext更晚
                items.add(wextToTimeline(wexts.get(wp))); // 选择wext
                wp++;
            } else {
                // 选择repost
                items.add(repostToTimeline(reposts.get(rp)));
                rp++;
            }
        }

        if (items.size() > maxSize) {   // 裁剪
            items = items.subList(0, maxSize);
        }
        log.debug(items.toString());
        return items;
    }

    /**
     * 拉取某人的timeline feed
     */
    public List<TimelineItem> pullTimelineOf(@NonNull Long userID,
                                             Long page, Integer pageSize, String lastWextID) {
        // timeline 最大500条
        List<String> feedIDs = new ArrayList<>();
        String feedKey = RedisKeyPrefixs.userTimelineFeedKeyPrefix + userID;

        if (!stringRedisTool.hasKey(feedKey)) {
            // 初始化的情况
            Long pullPage = 1L;
            Integer pullPageSize = 500;
            List<Long> followings = userService.getUserFollowingIds(userID, pullPage, pullPageSize);
            while (!followings.isEmpty()) { // 取得所有关注用户融合后的feedID
                feedIDs = mergeFeedIDs(feedIDs, mergeUserFeed(followings), 500);    // reduce逻辑

                pullPage++;
                followings = userService.getUserFollowingIds(userID, pullPage, pullPageSize);
            }

            // 保存timeline到redis
            feedIDs.forEach(feedID ->
                    stringRedisTool.zsetAdd(feedKey, feedID, -FeedTool.getTimestampFromFeedID(feedID).doubleValue()));  // 放入到redis中，score为负数
            stringRedisTool.expire(feedKey, timelineFeedTTl);

        }

        if (lastWextID != null) {
            feedIDs = new ArrayList<>(stringRedisTool.zsetGetByRange(feedKey, 0, 499));
            feedIDs = FeedTool.feedIDFilter(feedIDs, lastWextID);
            if (feedIDs.size() > pageSize) {
                feedIDs = feedIDs.subList(0, pageSize);
            }
        } else {
            long start = pageSize * (page - 1);
            long end = pageSize * page;
            feedIDs = new ArrayList<>(stringRedisTool.zsetGetByRange(feedKey, start, end));
        }

//        return feedIDs.stream() // 将id转换为timelineItem
////                .map(this::getTimelineItem)
////                .collect(Collectors.toList());
        return getTimelineItems(feedIDs);
    }

    /**
     * 归并列表中的用户的feed流
     *
     * @param userIDs 关注用户的id
     * @return 合并后的feedIDs
     */
    private List<String> mergeUserFeed(@NonNull List<Long> userIDs) {
        return userIDs.stream()
                .map(this::pullUserProfileFeedIDs)
                .reduce((userFeedIDs_1, userFeedIDs_2) ->
                        new ArrayList<>(mergeFeedIDs(userFeedIDs_1, userFeedIDs_2, 500)))
                .orElse(new ArrayList<>());
    }

    private List<String> mergeFeedIDs(@NonNull List<String> feed_1, @NonNull List<String> feed_2, int maxSize) {
        List<String> resultFeed = new ArrayList<>();
        int p_1 = 0;
        int p_2 = 0;

        while (resultFeed.size() < maxSize) {
            if (p_1 == feed_1.size()) {
                resultFeed.addAll(feed_2.subList(p_2, feed_2.size()));
                break;
            }
            if (p_2 == feed_2.size()) {
                resultFeed.addAll(feed_1.subList(p_1, feed_1.size()));
                break;
            }
            if (FeedTool.getTimestampFromFeedID(feed_1.get(p_1)) > FeedTool.getTimestampFromFeedID(feed_2.get(p_2))) {
                // 1号的更新，选择1号
                resultFeed.add(feed_1.get(p_1));
                p_1++;
            } else {
                resultFeed.add(feed_2.get(p_2));
                p_2++;
            }
        }

        if (resultFeed.size() > maxSize) {
            resultFeed = resultFeed.subList(0, maxSize);
        }
        log.debug(resultFeed.toString());
        return resultFeed;
    }

    private TimelineItem wextToTimeline(WextDTO wext) {
        return TimelineItem.builder()
                .wext(wext)
                .isRepost(false)
                .user(pullUserInfo(wext.getUserId()))
                .repostUser(null)
                .createdTime(wext.getCreatedTime())
                .build();
    }

    private TimelineItem repostToTimeline(RepostDTO repost) {
        WextDTO w = pullWext(repost.getWextId());
        return TimelineItem.builder()
                .wext(w)
                .isRepost(true)
                .user(pullUserInfo(w.getUserId()))
                .repostUser(pullUserInfo(repost.getUserId()))
                .createdTime(repost.getCreatedTime())
                .build();
    }


}
