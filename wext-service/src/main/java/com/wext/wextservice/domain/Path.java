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
public class Path implements Serializable {

    private static final long serialVersionUID = 6034058776775783682L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "path_id")
    private Long id;

    @Column(name = "node_name", nullable = false)
    private String nodeName;

    @Column(name = "full_path", nullable = false, unique = true)
    private String fullPath;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "created_time", columnDefinition = "datetime", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdTime;

}
