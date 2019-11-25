package com.wext.userservice.controller;

import com.wext.common.domain.BaseResponse;
import com.wext.common.domain.exception.AuthorityLimitException;
import com.wext.common.domain.exception.InvalidOperationException;
import com.wext.common.domain.exception.UserTargetException;
import com.wext.common.domain.request.ContentRequest;
import com.wext.common.domain.request.ListRequest;
import com.wext.userservice.domain.DirectMessage;
import com.wext.userservice.domain.User;
import com.wext.userservice.service.DirectMessageService;
import com.wext.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
@Slf4j
public class DirectMessageController {

    private DirectMessageService dmService;
    private UserService userService;

    private static final String USERID_HEADER = "X-data-userID";

    @Autowired
    public DirectMessageController(DirectMessageService dmService, UserService userService) {
        this.dmService = dmService;
        this.userService = userService;
    }

    @GetMapping("/message/{id}")
    public ResponseEntity getDMDetail(@PathVariable Long id,
                                      @RequestHeader(USERID_HEADER) String userID) {
        DirectMessage message = dmService.getDM(id);

        if (message.getUserIdFrom().toString().equals(userID) ||
                message.getUserIdTo().toString().equals(userID)) {
            return ResponseEntity.ok(
                    BaseResponse.successResponse(message)
            );
        } else {
            throw new AuthorityLimitException("You do not have the authority of this message");
        }
    }

    @DeleteMapping("/message/{id}")
    public ResponseEntity deleteDMDetail(@PathVariable Long id,
                                         @RequestHeader(USERID_HEADER) String userID) {
        DirectMessage message = dmService.getDM(id);

        if (message.getUserIdFrom().toString().equals(userID) ||
                message.getUserIdTo().toString().equals(userID)) {    // 只有发出或接收者可以删除私信

            dmService.deleteDM(id);
            return ResponseEntity.ok(
                    BaseResponse.successResponse(null)
            );
        } else {
            throw new AuthorityLimitException("You do not have the authority of this message");
        }
    }

    @PutMapping("/")
    public ResponseEntity createDM(@RequestHeader(USERID_HEADER) String userID,
                                   @RequestBody ContentRequest contentRequest) {
        Long fromUserID = Long.parseLong(userID);
        Long toUserID;

        // 获取目标用户id
        if (contentRequest.getId() != null) {
            toUserID = Long.parseLong(contentRequest.getId());
            User to = userService.getUserById(toUserID);    // 确认目标用户存在
        } else if (contentRequest.getScreenName() != null) {
            User to = userService.getUserAutoChoose(contentRequest.getScreenName());
            toUserID = to.getId();
        } else {
            throw new UserTargetException("Can not find the target user");
        }

        if (contentRequest.getContent() == null || contentRequest.getContent().isEmpty()) {
            throw new InvalidOperationException("Need content.");
        }

        DirectMessage dm = dmService.createNewDM(fromUserID, toUserID, contentRequest.getContent());
        return ResponseEntity.ok(
                BaseResponse.successResponse(dm)
        );

    }

    @GetMapping("/receives")
    public ResponseEntity getReceiveDM(@RequestHeader(USERID_HEADER) String userID,
                                   @RequestParam(required = false, defaultValue = "1") Integer page) {
        List<DirectMessage> dms = dmService.getUserReceiveDM(Long.parseLong(userID), page);
        return ResponseEntity.ok(
                BaseResponse.successResponse(dms)
        );
    }

    @GetMapping("/sends")
    public ResponseEntity getSendDM(@RequestHeader(USERID_HEADER) String userID,
                                    @RequestParam(required = false, defaultValue = "1") Integer page) {
        List<DirectMessage> dms = dmService.getUserSendDM(Long.parseLong(userID), page);
        return ResponseEntity.ok(
                BaseResponse.successResponse(dms)
        );
    }

    @PostMapping("/read")
    public ResponseEntity readDM(@RequestHeader(USERID_HEADER) String userID,
                                 @RequestBody ListRequest<Long> request) {

        dmService.readDM(request.getList(), Long.parseLong(userID));

        return ResponseEntity.ok(
                BaseResponse.successResponse(null)
        );
    }

}
