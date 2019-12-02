package com.wext.wextservice.service;

import com.wext.common.domain.exception.InvalidOperationException;
import com.wext.common.domain.exception.NonExistentException;
import com.wext.common.utils.WextTool;
import com.wext.wextservice.client.ManagerService;
import com.wext.wextservice.domain.PRState;
import com.wext.wextservice.domain.PathRequest;
import com.wext.wextservice.repository.PathRepository;
import com.wext.wextservice.repository.PathRequestRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;

@Service
@Slf4j
public class PathRequestService {

    @Autowired
    private PathRequestRepository requestRepository;

    @Autowired
    private PathRepository pathRepository;

    @Autowired
    private ManagerService managerService;

    private static final Integer defaultPageSize = 30;

    public PathRequest getPR(@NonNull Long prID) {
        return requestRepository.findById(prID)
                .orElseThrow(() -> new NonExistentException("Can not find the PR of " + prID));
    }

    public PathRequest createPR(@NonNull Long applicantID, @NonNull String fullPath, String requestComment,
                                PRState state) {
        fullPath = fullPath.toLowerCase();
        var paths = WextTool.splitPath(fullPath);
        if (paths.size() < 2 ||
                pathRepository.findByFullPath(paths.get(paths.size() - 2)).isEmpty()) {
            throw new InvalidOperationException("Parent path is not existing.");
        }
        if (pathRepository.findByFullPath(fullPath).isPresent()) {
            throw new InvalidOperationException("The Path is existing now.");
        }
        var prExistCheck = requestRepository.findByFullPath(fullPath);
        if (prExistCheck.isPresent()) {
            throw new InvalidOperationException("The Path Request is existing now. Created at " +
                    prExistCheck.get().getCreatedTime());
        }

        if (state == null) {
            state = PRState.WAITING;
        }

        var pr = requestRepository.save(
                PathRequest.builder()
                        .applicantID(applicantID)
                        .fullPath(fullPath)
                        .requestComment(requestComment)
                        .state(state.value())
                        .build()
        );
        log.info("Path Request created: " + pr);
        return pr;
    }

    public PathRequest updatePR(@NonNull Long prID,
                                @NonNull Long managerID, String manageComment,
                                PRState state) {
        var pathRequest = requestRepository.findById(prID)
                .orElseThrow(() -> new NonExistentException("Can't find the path request of " + prID));
        pathRequest.setManagerID(managerID);
        pathRequest.setManageComment(manageComment);
        if (state != null) {
            pathRequest.setState(state.value());
        }
        pathRequest = requestRepository.save(pathRequest);
        log.info("PR updated:" +
                ". manager:" + managerID +
                ". manage comment:" + manageComment +
                ". state:" + (state == null ? "null" : state.value()));
        return pathRequest;
    }

    public Map<String, Object> userRequesting(@NonNull Long userID,
                                              PRState state, @NonNull Sort.Direction timeDirection,
                                              Integer page, Integer pageSize) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = defaultPageSize;
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize,
                new Sort(timeDirection, "createdTime"));

        Page<PathRequest> que;
        if (state == null) {
            que = requestRepository.findAllByApplicantID(userID, pageable);
        } else {
            que = requestRepository.findAllByApplicantIDAndState(userID, state.value(), pageable);
        }
        Map<String, Object> res = new TreeMap<>();
        res.put("total_pages", que.getTotalPages());
        res.put("requests", que.getContent());

        // 添加manager信息
        Map<Long, Object> managers = new TreeMap<>();
        for (var request : que.getContent()) {
            var mid = request.getManagerID();
            if (mid != null && !managers.containsKey(mid)) {
                var m = managerService.getManagerById(mid);
                Map<String, Object> mInfo = new TreeMap<>();
                mInfo.put("name", m.getName());
                managers.put(mid, mInfo);
            }
        }
        res.put("managers", managers);
        log.debug(res.toString());
        return res;
    }

    public Map<String, Object> allRequests(PRState state, @NonNull Sort.Direction timeDirection,
                                           Integer page, Integer pageSize) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = defaultPageSize;
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize,
                new Sort(timeDirection, "createdTime"));

        Page<PathRequest> que;
        if (state == null) {
            que = requestRepository.findAll(pageable);
        } else {
            que = requestRepository.findAllByState(state.value(), pageable);
        }

        Map<String, Object> res = new TreeMap<>();
        res.put("total_pages", que.getTotalPages());
        res.put("requests", que.getContent());

        // 添加manager信息
        Map<Long, Object> managers = new TreeMap<>();
        for (var request : que.getContent()) {
            var mid = request.getManagerID();
            if (mid != null && !managers.containsKey(mid)) {
                var m = managerService.getManagerById(mid);
                Map<String, Object> mInfo = new TreeMap<>();
                mInfo.put("name", m.getName());
                managers.put(mid, mInfo);
            }
        }
        res.put("managers", managers);
        log.debug(res.toString());
        return res;
    }
}
