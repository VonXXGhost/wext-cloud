package com.wext.userservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RelationshipEntity(type = "follow")
public class Follow implements Serializable {

    private static final long serialVersionUID = -9204639494167001897L;

    @Id
    @GeneratedValue
    private Long id;

    @Property
    private Long createTime;

    @StartNode
    private UserNode fromUser;

    @EndNode
    private UserNode toUser;

}
