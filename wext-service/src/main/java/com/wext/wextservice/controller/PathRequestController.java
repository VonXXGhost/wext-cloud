package com.wext.wextservice.controller;

import com.wext.common.domain.BaseResponse;
import com.wext.common.domain.exception.InvalidOperationException;
import com.wext.common.domain.request.ListRequest;
import com.wext.wextservice.domain.PRState;
import com.wext.wextservice.service.PathRequestService;
import com.wext.wextservice.service.PathService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/path_request")
@Slf4j
public class PathRequestController {

    private static final String USERID_HEADER = "X-data-userID";

    @Autowired
    private PathRequestService requestService;

    @Autowired
    private PathService pathService;

    /**
     * 建立新PR的请求体
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class newPathRequest implements Serializable {

        @NotNull(message = "'fullPath' can not be null.")
        @Size(min = 2, max = 255, message = "Path is too long or short.")
        private String fullPath;

        @NotNull(message = "'comment' can not be null.")
        @Size(min = 30, max = 1500, message = "The length of comment should be between 30 and 1500.")
        private String comment;
    }

    @PutMapping("/")
    public ResponseEntity newPR(@RequestHeader(USERID_HEADER) Long userID,
                                @RequestBody @Valid newPathRequest request, BindingResult results) {
        if (results.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    BaseResponse.failResponse(Objects.requireNonNull(results.getFieldError()).getDefaultMessage())
            );
        }
        var pr = requestService.createPR(userID, request.getFullPath(), request.getComment(), PRState.WAITING);
        log.debug(pr.toString());
        return ResponseEntity.ok(
                BaseResponse.successResponse(pr)
        );
    }

    @GetMapping("/requesting")
    public ResponseEntity getRequestingForUser(@RequestHeader(USERID_HEADER) Long userID,
                                               @RequestParam(required = false, defaultValue = "all") String state,
                                               @RequestParam(required = false, defaultValue = "false") Boolean asc,
                                               @RequestParam(required = false, defaultValue = "1") Integer page,
                                               @RequestParam(required = false, defaultValue = "30") Integer pageSize) {
        PRState prState = null;
        if (!state.equals("all")) {
            if (PRState.contains(state)) {
                prState = PRState.valueOf(state);
            } else {
                throw new InvalidOperationException("state argument is wrong");
            }
        }

        return ResponseEntity.ok(
                BaseResponse.successResponse(
                        requestService.userRequesting(userID, prState,
                                asc ? Sort.Direction.ASC : Sort.Direction.DESC,
                                page, pageSize)
                )
        );
    }

    @GetMapping("/manage/requests")
    public ResponseEntity getRequestingForManager(@CookieValue(value = "managerID") Long managerID,
                                                  @RequestParam(required = false, defaultValue = "all") String state,
                                                  @RequestParam(required = false, defaultValue = "false") Boolean asc,
                                                  @RequestParam(required = false, defaultValue = "1") Integer page,
                                                  @RequestParam(required = false, defaultValue = "30") Integer pageSize) {
        PRState prState = null;
        if (!state.equals("all")) {
            if (PRState.contains(state)) {
                prState = PRState.valueOf(state);
            } else {
                throw new InvalidOperationException("state argument is wrong");
            }
        }

        return ResponseEntity.ok(
                BaseResponse.successResponse(
                        requestService.allRequests(prState,
                                asc ? Sort.Direction.ASC : Sort.Direction.DESC,
                                page, pageSize)
                )
        );
    }

    @PostMapping("/manage/dispatch")
    public ResponseEntity dispatchManager(@RequestBody ListRequest<Long> listRequest,
                                          @CookieValue(value = "managerID") Long managerID) {
        var res = listRequest.getList()
                .stream()
                .map(prID -> requestService.updatePR(prID, managerID, null, PRState.PROCESSING));
        return ResponseEntity.ok(
                BaseResponse.successResponse(res)
        );
    }

    /**
     * @param body {comment:xxx}
     */
    @PostMapping("/manage/{prID}/allow")
    @Transactional
    public ResponseEntity allowPR(@PathVariable Long prID,
                                  @CookieValue(value = "managerID") Long managerID,
                                  @RequestBody Map<String, String> body) {
        var path = pathService.createPath(requestService.getPR(prID).getFullPath());
        var pr = requestService.updatePR(prID, managerID, body.get("comment"), PRState.SUCCESS);
        return ResponseEntity.ok(
                BaseResponse.successResponse(pr)
        );
    }

    /**
     * @param body {comment:xxx}
     */
    @PostMapping("/manage/{prID}/reject")
    public ResponseEntity rejectPR(@PathVariable Long prID,
                                  @CookieValue(value = "managerID") Long managerID,
                                  @RequestBody Map<String, String> body) {
        var pr = requestService.updatePR(prID, managerID, body.get("comment"), PRState.REJECT);
        return ResponseEntity.ok(
                BaseResponse.successResponse(pr)
        );
    }
}
