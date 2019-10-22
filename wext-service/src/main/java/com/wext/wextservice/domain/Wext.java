package com.wext.wextservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wext implements Serializable {

    private static final long serialVersionUID = -1199025361201666535L;

    @Id
    @Column(name = "wext_id")
    private String id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "full_path", nullable = false)
    private String fullPath;

    private String text;

    @JsonIgnore
    private String pics;

    @Transient
    private List<String> picList;

    @Column(name = "repost_count")
    private Long repostCount = 0L;

    @Column(name = "comment_count")
    private Long commentCount = 0L;

    @Column(name = "like_count")
    private Long likeCount = 0L;

    @Column(name = "created_time", columnDefinition = "datetime", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdTime;
}
