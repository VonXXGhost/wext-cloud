package com.wext.userservice.repository;

import com.wext.userservice.domain.Follow;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends Neo4jRepository<Follow, Long> {

    @Query("match (n:UserNode{id:{fromId}})-[r:follow]->(m:UserNode{id:{toId}}) return count(r)>0 as c")
    Boolean existsFollowBetween(@Param("fromId") Long fromId, @Param("toId") Long toId);

    @Query("MATCH (n:UserNode{id:{fromId}})-[r:follow]->(m:UserNode{id:{toId}}) DELETE r ")
    void deleteFollowBetween(@Param("fromId") Long fromId, @Param("toId") Long toId);

}
