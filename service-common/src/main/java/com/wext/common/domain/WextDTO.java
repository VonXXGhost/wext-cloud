package com.wext.common.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WextDTO implements Serializable {

    private static final long serialVersionUID = 8019277399073512019L;

    private String id;

    private Long userId;

    private String fullPath;

    private String text;

    private String pics;

    private List<String> picList;

    private Long repostCount;

    private Long commentCount;

    private Long likeCount;

    private Date createdTime;
}
