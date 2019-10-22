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
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 9193306210605919091L;

    private Long id;

    private String password;

    private String screenName;

    private String nickname;

    private String email;

    private String profile;

    private String iconFilename;

    private Date createdTime;

    private Date lastUpdateTime;

}

