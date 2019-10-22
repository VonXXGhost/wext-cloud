package com.wext.userservice.domain;

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
public class DirectMessage implements Serializable {

    private static final long serialVersionUID = -6395395444269065239L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dm_id")
    private Long id;

    @Column(name = "user_id_from", nullable = false)
    private Long userIdFrom;

    @Column(name = "user_id_to", nullable = false)
    private Long userIdTo;

    private String content;

    @Column(name = "have_read", nullable = false)
    private Boolean haveRead;

    @Column(name = "created_time", columnDefinition = "datetime", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdTime;
}
