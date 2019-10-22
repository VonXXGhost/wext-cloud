package com.wext.userservice.repository;

import com.wext.userservice.domain.UserNode;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserNodeRepository extends Neo4jRepository<UserNode, Long> {
    Optional<UserNode> findUserNodeById(Long id);

    @Query("match (n:UserNode{id:{id}}) RETURN size((n)-[:follow]->(:UserNode))")
    Optional<Long> getFollowCount(@Param("id") Long id);

    @Query("match (n:UserNode{id:{id}}) RETURN size((:UserNode)-[:follow]->(n))")
    Optional<Long> getFollowerCount(@Param("id") Long id);

    @Query("MATCH (n:UserNode{id:{id}})-[r:follow]->(d:UserNode) " +
            "RETURN d ORDER BY r.createTime DESC SKIP {skip} LIMIT {limit}")
    Optional<List<UserNode>> findUserFollowings(@Param("id") Long id,
                                                @Param("skip") Long skip,
                                                @Param("limit") Integer limit);

    @Query("MATCH (n:UserNode)-[r:follow]->(d:UserNode{id:{id}}) " +
            "RETURN n ORDER BY r.createTime DESC SKIP {skip} LIMIT {limit}")
    Optional<List<UserNode>> findUserFollowers(@Param("id") Long id,
                                               @Param("skip") Long skip,
                                               @Param("limit") Integer limit);
}
