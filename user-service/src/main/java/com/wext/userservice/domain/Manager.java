package com.wext.userservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Manager implements Serializable {

    private static final long serialVersionUID = 5314180554636135701L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mg_id")
    private Long id;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Length(max = 32)
    @Column(name = "mg_name", unique = true, nullable = false)
    private String name;

    @Length(max = 32)
    private String role;

    @Column(name = "created_time", columnDefinition = "datetime", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdTime;

    @Column(name = "last_update_time", columnDefinition = "datetime")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date lastUpdateTime;
}
