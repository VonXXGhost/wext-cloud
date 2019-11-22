package com.wext.wextservice.controller;


import com.wext.common.domain.BaseResponse;
import com.wext.common.domain.RepostDTO;
import com.wext.common.domain.TimelineItem;
import com.wext.common.domain.WextDTO;
import com.wext.common.domain.exception.AuthorityLimitException;
import com.wext.common.domain.exception.InvalidOperationException;
import com.wext.common.domain.exception.NonExistentException;
import com.wext.common.domain.request.ContentRequest;
import com.wext.common.domain.request.NewWextRequest;
import com.wext.common.utils.CommonTool;
import com.wext.common.utils.FeedTool;
import com.wext.wextservice.client.Puller;
import com.wext.wextservice.client.Pusher;
import com.wext.wextservice.domain.Comment;
import com.wext.wextservice.domain.Like;
import com.wext.wextservice.domain.Repost;
import com.wext.wextservice.domain.Wext;
import com.wext.wextservice.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/wext")
@Slf4j
public class WextController {

    private WextService wextService;
    private Pusher pusher;
    private Puller puller;
    private RepostService repostService;
    private LikeService likeService;
    private CommentService commentService;
    private PathService pathService;

    private static final String USERID_HEADER = "X-data-userID";

    @Autowired
    public WextController(WextService wextService, Pusher pusher, Puller puller, RepostService repostService, LikeService likeService, CommentService commentService, PathService pathService) {
        this.wextService = wextService;
        this.pusher = pusher;
        this.puller = puller;
        this.repostService = repostService;
        this.likeService = likeService;
        this.commentService = commentService;
        this.pathService = pathService;
    }

    @GetMapping("/{wextID}")
    public ResponseEntity getWextDetail(@PathVariable String wextID) {
        WextDTO wext = puller.pullWext(wextID);
        TimelineItem item = puller.getTimelineItem(FeedTool.geneFeedID(wext));
        return ResponseEntity.ok(
                BaseResponse.successResponse(item)
        );
    }

    @GetMapping("/{wextID}/reposts")
    public ResponseEntity getWextReposts(@PathVariable String wextID,
                                          @RequestParam(required = false, defaultValue = "1") Integer page,
                                          @RequestParam(required = false, defaultValue = "30") Integer pageSize) {
        Map<String, Object> reposts = repostService.getRepostsOfWext(wextID, page, pageSize);
        return ResponseEntity.ok(
                BaseResponse.successResponse(reposts)
        );
    }

    @GetMapping("/{wextID}/comments")
    public ResponseEntity getWextComments(@PathVariable String wextID,
                                          @RequestParam(required = false, defaultValue = "1") Integer page,
                                          @RequestParam(required = false, defaultValue = "30") Integer pageSize) {
        Map<String, Object> comments = commentService.getCommentOfWext(wextID, page, pageSize);
        return ResponseEntity.ok(
                BaseResponse.successResponse(comments)
        );
    }

    @GetMapping("/{wextID}/likes")
    public ResponseEntity getWextLikes(@PathVariable String wextID,
                                       @RequestParam(required = false, defaultValue = "1") Integer page,
                                       @RequestParam(required = false, defaultValue = "50") Integer pageSize) {
        Map<String, Object> likes = likeService.getLikesOfWext(wextID, page, pageSize);
        return ResponseEntity.ok(
                BaseResponse.successResponse(likes)
        );
    }

    @PutMapping("/")
    public ResponseEntity postNewWext(@RequestBody @Valid NewWextRequest request, BindingResult results,
                                      @RequestHeader(USERID_HEADER) Long userID) {
        if (results.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    BaseResponse.failResponse(results.getFieldError().getDefaultMessage())
            );
        }
        if (pathService.getPath(request.getPath()) == null) {
            throw new NonExistentException("Can not find this path.");
        }

        Wext wext = wextService.createWext(
                userID,
                request.getContent(),
                request.getPath(),
                request.getPicURLs()
        );

        // 推送
        pusher.pushWext(CommonTool.transBean(wext, WextDTO.class));

        return ResponseEntity.ok(
                BaseResponse.successResponse(wext)
        );
    }

    @DeleteMapping("/{wextID}")
    public ResponseEntity deleteWext(@PathVariable String wextID,
                                     @RequestHeader(USERID_HEADER) Long userID) {
        Wext wext = wextService.getWext(wextID);
        if (!wext.getUserId().equals(userID)) { // 只能删除自己的
            throw new AuthorityLimitException("Can not delete other's post.");
        }

        wextService.deleteWext(wextID); // 删除数据库数据
        pusher.pushDelete(CommonTool.transBean(wext, WextDTO.class));    // 推送删除动作

        return ResponseEntity.ok(
                BaseResponse.successResponse(null)
        );
    }

    @PutMapping("/{wextID}/repost")
    public ResponseEntity putNewRepost(@PathVariable String wextID,
                                       @RequestHeader(USERID_HEADER) Long userID) {
        wextService.getWext(wextID);    // 检查wext是否存在

        if (repostService.hasRepost(userID, wextID)) {
            throw new InvalidOperationException("Can not repost again");
        }

        Repost repost = repostService.createRepost(userID, wextID);
        TimelineItem item = puller.getTimelineItem(
                FeedTool.geneFeedID(
                        Objects.requireNonNull(CommonTool.transBean(repost, RepostDTO.class))));

        // 推送
        pusher.pushRepost(CommonTool.transBean(repost, RepostDTO.class));
        pusher.updateWext(CommonTool.transBean(
                wextService.getWext(wextID), WextDTO.class)
        );

        return ResponseEntity.ok(
                BaseResponse.successResponse(item)
        );
    }

    @DeleteMapping("/{wextID}/repost")
    public ResponseEntity deleteRepost(@PathVariable String wextID,
                                       @RequestHeader(USERID_HEADER) Long userID) {

        Repost repost = repostService.getRepost(userID, wextID);

        repostService.deleteRepost(repost.getId());
        pusher.pushDelete(CommonTool.transBean(repost, RepostDTO.class));
        pusher.updateWext(CommonTool.transBean(
                wextService.getWext(wextID), WextDTO.class));

        return ResponseEntity.ok(
                BaseResponse.successResponse(null)
        );
    }

    @PutMapping("/{wextID}/comment")
    public ResponseEntity putNewComment(@PathVariable String wextID,
                                        @RequestBody ContentRequest request,
                                        @RequestHeader(USERID_HEADER) Long userID) {
        String content = request.getContent();
        if (content == null || content.length() == 0) {
            throw new InvalidOperationException("Empty content");
        } else if (content.length() > 300) {
            throw new InvalidOperationException("Comment too long");
        }

        Comment comment = commentService.createComment(userID, wextID, content);
        pusher.updateWext(CommonTool.transBean(
                wextService.getWext(wextID), WextDTO.class));

        return ResponseEntity.ok(
                BaseResponse.successResponse(comment)
        );
    }

    @DeleteMapping("/{wextID}/comment/{commentID}")
    public ResponseEntity deleteComment(@PathVariable String wextID,
                                        @PathVariable Long commentID,
                                        @RequestHeader(USERID_HEADER) Long userID) {
        Comment comment = commentService.getCommentById(commentID);
        if (!comment.getUserId().equals(userID)) {
            throw new AuthorityLimitException("The comment not create by you");
        }
        commentService.deleteComment(commentID);

        return ResponseEntity.ok(
                BaseResponse.successResponse(null)
        );
    }

    @PutMapping("/{wextID}/like")
    public ResponseEntity putNewLike(@PathVariable String wextID,
                                     @RequestHeader(USERID_HEADER) Long userID) {
        wextService.getWext(wextID);    // 检查wext是否存在
        Like like = likeService.createLike(userID, wextID);
        pusher.updateWext(CommonTool.transBean(
                wextService.getWext(wextID), WextDTO.class));

        return ResponseEntity.ok(
                BaseResponse.successResponse(like)
        );
    }

    @DeleteMapping("/{wextID}/like")
    public ResponseEntity deleteLike(@PathVariable String wextID,
                                     @RequestHeader(USERID_HEADER) Long userID) {
        if (!likeService.hasLike(userID, wextID)) {
            throw new InvalidOperationException("Never like this wext.");
        }
        likeService.deleteLike(userID, wextID);
        pusher.updateWext(CommonTool.transBean(
                wextService.getWext(wextID), WextDTO.class));

        return ResponseEntity.ok(
                BaseResponse.successResponse(null)
        );
    }

    @GetMapping("/{wextID}/like/has")
    public ResponseEntity hasLike(@PathVariable String wextID,
                                  @RequestHeader(USERID_HEADER) Long userID) {
        return ResponseEntity.ok(
                BaseResponse.successResponse(likeService.hasLike(userID, wextID))
        );
    }

    @GetMapping("/{wextID}/repost/has")
    public ResponseEntity hasRepost(@PathVariable String wextID,
                                  @RequestHeader(USERID_HEADER) Long userID) {
        return ResponseEntity.ok(
                BaseResponse.successResponse(repostService.hasRepost(userID, wextID))
        );
    }

}
