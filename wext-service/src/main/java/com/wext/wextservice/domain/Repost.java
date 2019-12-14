package com.wext.wextservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Repost implements Serializable {

    private static final long serialVersionUID = 6122498384825463171L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repost_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "wext_id", nullable = false)
    private String wextId;

    @Column(name = "created_time", columnDefinition = "datetime", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    /* 注意：MySQL版本必须高于5.6.4并定义为datetime(3)，否则会丢失精度造成严重的业务错误 */
    private Date createdTime;
}

