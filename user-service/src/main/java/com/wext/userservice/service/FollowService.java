package com.wext.userservice.service;

import com.wext.userservice.domain.Follow;
import com.wext.userservice.domain.UserNode;
import com.wext.common.domain.exception.InvalidOperationException;
import com.wext.common.domain.exception.NonExistentException;
import com.wext.userservice.repository.FollowRepository;
import com.wext.userservice.repository.UserNodeRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
public class FollowService {

    private FollowRepository followRepository;
    private UserNodeRepository userNodeRepository;

    @Autowired
    public FollowService(FollowRepository followRepository, UserNodeRepository userNodeRepository) {
        this.followRepository = followRepository;
        this.userNodeRepository = userNodeRepository;
    }

    @Transactional
    public Follow createFollow(@NonNull Long fromId, @NonNull Long toId) {

        if (fromId.equals(toId)) {  // 不能关注自己
            throw new InvalidOperationException("Can't change relationship for self.");
        }
        if (followRepository.existsFollowBetween(fromId, toId)) {   // 合法判断
            throw new InvalidOperationException("Have followed.");
        }

        UserNode from = userNodeRepository.findUserNodeById(fromId)
                .orElseThrow(() -> new NonExistentException("Can't found the id"));
        UserNode to = userNodeRepository.findUserNodeById(toId)
                .orElseThrow(() -> new NonExistentException("Can't found the id"));

        Follow follow = Follow.builder()
                .fromUser(from)
                .toUser(to)
                .createTime(new Date().getTime())
                .build();

        log.info("Creating follow " + toId + " by " + fromId);
        return followRepository.save(follow);
    }

    @Transactional
    public void destroyFollow(@NonNull Long fromId, @NonNull Long toId) {
        if (fromId.equals(toId)) {  // 不能取关自己
            throw new InvalidOperationException("Can't change relationship for self.");
        }
        if (!followRepository.existsFollowBetween(fromId, toId)) {  // 合法判断
            throw new InvalidOperationException("Not following.");
        }
        followRepository.deleteFollowBetween(fromId, toId);
        log.info("Destroy follow " + toId + " by " + fromId);
    }

    public String getRelationshipBetween(@NonNull Long fromId, @NonNull Long toId) {
        Boolean following = followRepository.existsFollowBetween(fromId, toId);
        Boolean followed = followRepository.existsFollowBetween(toId, fromId);
        if (following && followed) {
            return "friend";
        } else if (following) {
            return "following";
        } else if (followed) {
            return "followed";
        } else {
            return "stranger";
        }
    }
}
