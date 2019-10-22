package com.wext.wextservice.service;

import com.wext.common.domain.exception.NonExistentException;
import com.wext.wextservice.domain.Repost;
import com.wext.wextservice.repository.RepostRepository;
import com.wext.wextservice.repository.WextRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class RepostService {

    private RepostRepository repostRepository;
    private WextRepository wextRepository;

    private static final Integer defaultPageSize = 100;

    @Autowired
    public RepostService(RepostRepository repostRepository, WextRepository wextRepository) {
        this.repostRepository = repostRepository;
        this.wextRepository = wextRepository;
    }

    public boolean hasRepost(@NonNull Long userID, @NonNull String wextID) {
        return repostRepository.existsByUserIdAndWextId(userID, wextID);
    }

    public Repost getRepost(@NonNull Long userID, @NonNull String wextID) {
        return repostRepository.findTopByUserIdAndWextId(userID, wextID)
                .orElseThrow(() -> new NonExistentException("Can not find the repost: " + userID + " in " + wextID));
    }

    @Transactional
    public Repost createRepost(@NonNull Long userID, @NonNull String wextID) {
        Repost repost = repostRepository.save(
                Repost.builder()
                        .userId(userID).wextId(wextID).build()
        );
        wextRepository.incrRepostCount(wextID); // wext侧计数自增
        log.info("Repost created: " + repost.toString());
        return repost;
    }

    @Transactional
    public void deleteRepost(@NonNull Long repostID) {
        Repost repost = repostRepository.findById(repostID)
                .orElseThrow(() -> new NonExistentException("Can not find the repost: " + repostID));
        repostRepository.deleteById(repostID);
        wextRepository.decrRepostCount(repost.getWextId()); // wext侧计数自减
        log.info("Repost deleted: " + repost);
    }

    @Transactional
    public void deleteRepost(@NonNull Long userID, @NonNull String wextID) {
        repostRepository.deleteByUserIdAndWextId(userID, wextID);
        wextRepository.decrRepostCount(wextID); // wext侧计数自减
        log.info("Repost deleted: " + userID + " in " + wextID);
    }

    public List<Repost> getRepostsFromUser(@NonNull Long userID,
                                           Integer page, Integer pageSize) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = defaultPageSize;
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        List<Repost> reposts = repostRepository.findAllByUserIdOrderByCreatedTimeDesc(userID, pageable).getContent();
        log.debug(reposts.toString());
        return reposts;
    }

    public List<Repost> getRepostsOfWext(@NonNull String wextID,
                                           Integer page, Integer pageSize) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = defaultPageSize;
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        List<Repost> reposts = repostRepository.findAllByWextIdOrderByCreatedTimeDesc(wextID, pageable).getContent();
        log.debug(reposts.toString());
        return reposts;
    }

    public int countRepostOfWext(@NonNull String wextID) {
        return repostRepository.countAllByWextId(wextID);
    }
}
