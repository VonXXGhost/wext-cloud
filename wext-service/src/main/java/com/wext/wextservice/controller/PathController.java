package com.wext.wextservice.controller;

import com.wext.common.bean.RedisTool;
import com.wext.common.domain.BaseResponse;
import com.wext.wextservice.domain.Path;
import com.wext.wextservice.domain.PathCount;
import com.wext.wextservice.service.PathService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/path")
@Slf4j
public class PathController {

    @Autowired
    private PathService pathService;

    @Value("${wext.keyTTL.hot-path-cache-hours}")
    private Integer cacheHours;

    @Value("${wext.path.hot-path-validDataHours}")
    private Integer validDataHours;

    @Autowired
    private RedisTool<PathCount> pathCountRedisTool;


    @GetMapping("/general/**")
    public ResponseEntity pathInfo(HttpServletRequest request,
                                   @RequestParam(required = false, defaultValue = "1") Integer page,
                                   @RequestParam(required = false, defaultValue = "30") Integer pageSize) {
        String fullPath = request.getServletPath().substring("/path/general".length());
        Path path = pathService.getPath(fullPath);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("info", path);

        Map<String, Object> children = new LinkedHashMap<>();
        var childPaths = pathService.getChildPaths(path.getId(), page, pageSize);
        children.put("total_pages", childPaths.getTotalPages());
        children.put("paths", childPaths.getContent());
        result.put("children", children);
        log.debug(result.toString());
        return ResponseEntity.ok(
                BaseResponse.successResponse(result)
        );
    }

    @GetMapping("/hot/**")
    public ResponseEntity hotChild(HttpServletRequest request,
                                   @RequestParam(required = false, defaultValue = "1") Integer page,
                                   @RequestParam(required = false, defaultValue = "30") Integer pageSize) {
//        var a = request.getServletPath();
        String fullPath = request.getServletPath().substring("/path/hot".length());
        var key = "wext:path:hot::" + fullPath;
        List<PathCount> pathCounts;
        if (pathCountRedisTool.hasKey(key)) {
            pathCounts = pathCountRedisTool.listGet(key, (page - 1) * pageSize, page * pageSize);
            if (pathCounts != null) {
                return ResponseEntity.ok(
                        BaseResponse.successResponse(pathCounts)
                );
            }
        }

        if (pageSize * page < 1000) {
            pathCounts = pathService.getHotChildPaths(fullPath, validDataHours, 1, pageSize * page);
            pathCountRedisTool.del(key);
            pathCountRedisTool.listRightPushAll(key, pathCounts);
            pathCountRedisTool.expire(key, cacheHours * 60 * 60);
            try {
                pathCounts = pathCounts.subList((page - 1) * pageSize,
                        Math.min((page * pageSize) - ((page - 1) * pageSize), pathCounts.size()));
            } catch (IndexOutOfBoundsException e) {
                pathCounts = null;
            }
        } else {
            pathCounts = pathService.getHotChildPaths(fullPath, validDataHours,
                    (page - 1) * pageSize, pageSize * page);
        }
        log.info("Hot paths of " + fullPath + " page " + page + ": " + pathCounts);
        return ResponseEntity.ok(
                BaseResponse.successResponse(pathCounts)
        );
    }
}
