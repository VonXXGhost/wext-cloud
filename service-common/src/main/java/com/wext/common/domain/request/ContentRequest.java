package com.wext.common.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentRequest implements Serializable {
    private static final long serialVersionUID = -6986746375915710855L;

    private String id;
    private String screenName;
    private String content;
}
