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
public class ManagerDTO implements Serializable {
    private static final long serialVersionUID = 5314180554636135701L;

    private Long id;
    private String password;
    private String name;
    private String role;
    private Date createdTime;
    private Date lastUpdateTime;
}
