package com.wext.common.utils;

import com.wext.common.domain.RepostDTO;
import com.wext.common.domain.WextDTO;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class FeedTool {
    public static Long getTimestampFromFeedID(@NonNull String feedID) {
        return Long.parseLong(feedID.split(":")[3]);
    }

    public static String getIDFromFeedID(@NonNull String feedID) {
        return feedID.split(":")[1];
    }

    public static List<String> feedIDFilter(List<String> feedIDs, String lastWextDTOID) {
        int pos = 0;
        for (String r : feedIDs) { // 获得last在查询结果中的位置
            if (getIDFromFeedID(r).equals(lastWextDTOID)) {
                break;
            } else {
                pos++;
            }
        }
        if (feedIDs.size() == pos) { // last不存在于查询结果中
            return feedIDs;
        }
        if (feedIDs.size() == pos - 1) { // 无新条目，返回空
            feedIDs.clear();
            return feedIDs;
        }
        List<String> result = feedIDs.subList(pos + 1, feedIDs.size());
        log.debug("After result: " + result.toString());
        return result;
    }

    public static String geneFeedID(@NonNull WextDTO i) {
        return "W:" + i.getId() + "::" + i.getCreatedTime().getTime();
    }

    public static String geneFeedID(@NonNull WextDTO wext, @NonNull Long reposterID, @NonNull Long time) {
        return "R:" + wext.getId() +
                ":" + reposterID +
                ":" + time;
    }

    public static String geneFeedID(@NonNull RepostDTO repost) {
        return "R:" + repost.getWextId() +
                ":" + repost.getUserId() +
                ":" + repost.getCreatedTime().getTime();
    }
}
