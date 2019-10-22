package com.wext.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineItem implements Serializable {

    private static final long serialVersionUID = -186544931044463029L;

    private WextDTO wext;
    private boolean isRepost;
    private UserInfoItem user;
    private UserInfoItem repostUser;    // 转发者
    private Date createdTime;            // 转发时间
}
