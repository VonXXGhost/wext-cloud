package com.wext.wextservice.service;

import com.wext.common.bean.RedisTool;
import com.wext.common.domain.exception.NonExistentException;
import com.wext.wextservice.domain.Comment;
import com.wext.wextservice.domain.Wext;
import com.wext.wextservice.repository.CommentRepository;
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
public class CommentService {

    private CommentRepository commentRepository;
    private WextRepository wextRepository;
    private RedisTool redisTool;

    private static final Integer defaultPageSize = 30;

    @Autowired
    public CommentService(CommentRepository commentRepository, WextRepository wextRepository, RedisTool redisTool) {
        this.commentRepository = commentRepository;
        this.wextRepository = wextRepository;
        this.redisTool = redisTool;
    }

    @Transactional
    public Comment createComment(@NonNull Long userID, @NonNull String wextID,
                                 String content) {
        Long floor;
        String key = "wext:comment:floor lock:" + wextID;
        Comment comment = Comment.builder()     // 先填入其他信息减少加锁后的工作量
                .userId(userID)
                .wextId(wextID)
                .content(content)
                .build();

        while (!redisTool.lock(key)) {}  // 加锁，等待到获取锁为止
        Wext wext = wextRepository.findById(wextID)
                .orElseThrow(() -> {
                    redisTool.del(key);     // 失败解锁
                    return new NonExistentException("Can not find the wext.");
                });
        floor = wext.getCommentCount() + 1; // 获得当前层数
        comment.setFloor(floor);
        comment = commentRepository.save(comment);
        wext.setCommentCount(wext.getCommentCount() + 1);   // comment成功建立后再修改wext侧计数
        wextRepository.saveAndFlush(wext);
        redisTool.del(key);     // 成功解锁

        log.info("New Comment: " + comment);
        return comment;
    }

    /**
     * @return {“comments”: List<Comment> 当前页评论列表,
     *          "total_pages": long 总页数}
     */
    public Map<String, Object> getCommentOfWext(@NonNull String wextID, Integer page, Integer pageSize) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = defaultPageSize;
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Page<Comment> p = commentRepository.findAllByWextIdOrderByFloorAsc(wextID, pageable);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total_pages", p.getTotalPages());
        result.put("comments", p.getContent());
        log.debug(result.toString());
        return result;
    }

    public Comment getCommentById(@NonNull Long commentID) {
        return commentRepository.findById(commentID)
                .orElseThrow(() -> new NonExistentException("Can not find the comment"));
    }

    public void deleteComment(@NonNull Long commentID) {
        commentRepository.deleteById(commentID);
        log.info("Comment deleted: " + commentID);
        // 删除时不修改wext表的计数，确保层数获取是唯一的
    }

}
