package com.wext.wextservice.controller;

import com.wext.common.domain.BaseResponse;
import com.wext.wextservice.domain.Path;
import com.wext.wextservice.service.PathService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/path")
@Slf4j
public class PathController {
    private PathService pathService;

    @Autowired
    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/**")
    public ResponseEntity pathInfo(HttpServletRequest request,
                                   @RequestParam(required = false, defaultValue = "1") Integer page,
                                   @RequestParam(required = false, defaultValue = "30") Integer pageSize) {
        String fullPath = request.getServletPath().substring(5);
        Path path = pathService.getPath(fullPath);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("info", path);

        result.put("children", pathService.getChildPaths(path.getId(), page, pageSize));
        log.debug(result.toString());
        return ResponseEntity.ok(
                BaseResponse.successResponse(result)
        );
    }

//    @RequestMapping(value = "/**",method = RequestMethod.OPTIONS)
//    public ResponseEntity handleOptions(){
//        return ResponseEntity.noContent().build();
//    }
}
