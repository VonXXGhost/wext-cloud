package com.wext.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepostDTO implements Serializable {
    private Long id;

    private Long userId;

    private String wextId;

    private Date createdTime;
}
