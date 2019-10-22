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
public class UserInfoItem implements Serializable {

    private static final long serialVersionUID = 6500047635548581025L;

    private Long id;
    private String screenName;
    private String nickname;
    private String email;
    private String profile;
    private String iconUrl;
    private Date createdTime;
    private Long followings;
    private Long followers;
}
