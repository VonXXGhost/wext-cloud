package com.wext.userservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity(label = "UserNode")
public class UserNode implements Serializable {

    private static final long serialVersionUID = 8927701532424150165L;

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long node_id;

    @Property
    private Long id;
}
