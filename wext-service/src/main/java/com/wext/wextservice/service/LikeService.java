package com.wext.wextservice.service;

import com.wext.common.domain.exception.InvalidOperationException;
import com.wext.common.domain.exception.NonExistentException;
import com.wext.wextservice.domain.Like;
import com.wext.wextservice.repository.LikeRepository;
import com.wext.wextservice.repository.WextRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Slf4j
public class LikeService {

    private LikeRepository likeRepository;
    private WextRepository wextRepository;

    private static final Integer defaultPageSize = 30;

    @Autowired
    public LikeService(LikeRepository likeRepository, WextRepository wextRepository) {
        this.likeRepository = likeRepository;
        this.wextRepository = wextRepository;
    }

    @Transactional
    public Like createLike(@NonNull Long userID, @NonNull String wextID) {
        if (likeRepository.existsByUserIdAndWextId(userID, wextID)) {
            throw new InvalidOperationException("Can not repeat like.");
        }
        Like like = likeRepository.save(
                Like.builder()
                        .userId(userID)
                        .wextId(wextID).build()
        );
        wextRepository.incrLikeCount(wextID);   // 自增统计
        log.info("Like created: " + like);
        return like;
    }

    public boolean hasLike(@NonNull Long userID, @NonNull String wextID) {
        return likeRepository.existsByUserIdAndWextId(userID, wextID);
    }

    @Transactional
    public void deleteLike(Like like) {
        likeRepository.deleteById(like.getId());
        wextRepository.decrLikeCount(like.getWextId()); // 自减统计
        log.info("Like delete: " + like);
    }

    @Transactional
    public void deleteLike(@NonNull Long userID, @NonNull String wextID) {
        Like like = likeRepository.findTopByUserIdAndWextId(userID, wextID)
                .orElseThrow(() -> new NonExistentException("Can not find the like."));
        deleteLike(like);
    }

    /**
     * @return {“likes”: List<Like> 当前页点赞列表,
     * "total_pages": long 总页数}
     */
    public Map<String, Object> getLikesOfWext(@NonNull String wextID, Integer page, Integer pageSize) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = defaultPageSize;
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Page<Like> p = likeRepository.findAllByWextIdOrderByCreatedTimeDesc(wextID, pageable);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total_pages", p.getTotalPages());
        result.put("likes", p.getContent());
        log.debug(result.toString());
        return result;
    }

    /**
     * @return {“likes”: List<Like> 当前页点赞列表,
     * "total_pages": long 总页数}
     */
    public Map<String, Object> getLikesOfUser(@NonNull Long userID, Integer page, Integer pageSize) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = defaultPageSize;
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Page<Like> p = likeRepository.findAllByUserIdOrderByCreatedTimeDesc(userID, pageable);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total_pages", p.getTotalPages());
        result.put("likes", p.getContent());
        log.debug(result.toString());
        return result;
    }

}
