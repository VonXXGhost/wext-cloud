package com.wext.wextservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PathRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pr_id")
    private Long id;

    @Column(name = "applicant_id", nullable = false)
    private Long applicantID;

    @Column(name = "full_path", nullable = false, unique = true)
    private String fullPath;

    @Column(name = "request_comment")
    private String requestComment;

    @Column(name = "manager_id")
    private Long managerID;

    @Column(name = "manage_comment")
    private String manageComment;

    @Column(nullable = false)
    private String state;

    @Column(name = "created_time", columnDefinition = "datetime", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdTime;

    @Column(name = "last_update_time", columnDefinition = "datetime")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date lastUpdateTime;
}
