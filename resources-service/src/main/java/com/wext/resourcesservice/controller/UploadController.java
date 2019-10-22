package com.wext.resourcesservice.controller;

import com.wext.common.domain.BaseResponse;
import com.wext.common.domain.exception.InvalidOperationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${upload.img-absolute-path}")
    private String absoluteImgPath;

    @Value("${upload.img-path}")
    private String imgPath;

    @Value("${upload.img-max-size}")
    private Long imgMaxSize;

    private static final String USERID_HEADER = "X-data-userID";

    @PostMapping("/image")
    public ResponseEntity uploadPic(MultipartFile file,
                                    @RequestHeader(USERID_HEADER) String userID,
                                    HttpServletRequest request) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new InvalidOperationException("Not found the file");
        } else if (file.getSize() > imgMaxSize) {    // 最大10MB
            throw new InvalidOperationException("File size is too big.");
        }
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1);
        Set<String> allowSuffix = Stream.of("png", "jpg", "gif").collect(Collectors.toSet());
        if (allowSuffix.contains(suffix)) {
            String filename = userID + new Date().getTime() + '.' + suffix;  // 用户名+时间戳+源后缀名
            File f = new File(absoluteImgPath, filename);
            file.transferTo(f);
            Map resp = new TreeMap();
            resp.put("path", imgPath + filename);
            return ResponseEntity.ok(
                    BaseResponse.successResponse(resp)
            );
        } else {
            throw new InvalidOperationException("Only allow upload jpg, png or gif.");
        }
    }

//    @RequestMapping(value = "/**",method = RequestMethod.OPTIONS)
//    public ResponseEntity handleOptions(){
//        return ResponseEntity.noContent().build();
//    }
}
