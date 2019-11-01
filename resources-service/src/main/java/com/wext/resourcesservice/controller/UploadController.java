package com.wext.resourcesservice.controller;

import com.wext.common.domain.BaseResponse;
import com.wext.common.domain.exception.InvalidOperationException;
import com.wext.resourcesservice.client.UserService;
import com.wext.resourcesservice.utils.ResourcesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/upload")
@Slf4j
public class UploadController {

    @Value("${upload.img-absolute-path}")
    private String absoluteImgPath;

    @Value("${upload.img-path}")
    private String imgPath;

    @Value("${upload.img-max-size}")
    private Long imgMaxSize;

    private static final String USERID_HEADER = "X-data-userID";

    @Autowired
    private UserService userService;

    @PostMapping("/image")
    public ResponseEntity uploadPic(MultipartFile file,
                                    @RequestHeader(USERID_HEADER) String userID) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new InvalidOperationException("Not found the file");
        } else if (file.getSize() > imgMaxSize) {    // 最大10MB
            throw new InvalidOperationException("File size is too big.");
        }
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1);
        Set<String> allowSuffix = Stream.of("png", "jpg", "gif").collect(Collectors.toSet());
        if (allowSuffix.contains(suffix)) {
            String filename = userID
                    + '_' + LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).toEpochMilli()
                    + '.' + suffix;  // 用户名+时间戳+源后缀名
            // 检查文件夹是否存在
            File f = getFileWithSubDirCheck(filename);
            file.transferTo(f);
            log.info("Pic saved: " + filename);
            Map<String, String> resp = new TreeMap<>();
            resp.put("path", imgPath + filename);
            return ResponseEntity.ok(
                    BaseResponse.successResponse(resp)
            );
        } else {
            throw new InvalidOperationException("Only allow upload jpg, png or gif.");
        }
    }

    @PostMapping("/profile_icon")
    public ResponseEntity uploadUserIcon(MultipartFile file,
                                         @RequestHeader(USERID_HEADER) String userID) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new InvalidOperationException("Not found the file");
        } else if (file.getSize() > 1048576) {    // 最大1MB
            throw new InvalidOperationException("File size is too big.");
        }
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1);
        Set<String> allowSuffix = Stream.of("png", "jpg").collect(Collectors.toSet());
        if (allowSuffix.contains(suffix)) {
            String filename = userID
                    + '_' + LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).toEpochMilli()
                    + '.' + suffix;  // 用户名+时间戳+源后缀名
            File f = getFileWithSubDirCheck(filename);
            log.info("Icon Pic saved: " + filename);
            file.transferTo(f);
            Map<String, String> attr = new TreeMap<>();
            attr.put("iconPath", imgPath + filename);
            // 更新数据库信息
            userService.updateUserAttr(Long.parseLong(userID), attr);
            return ResponseEntity.ok(
                    BaseResponse.successResponse(attr)
            );
        } else {
            throw new InvalidOperationException("Only allow upload jpg, png.");
        }
    }

    private File getFileWithSubDirCheck(String filename) {
        
        var subDir = new File(absoluteImgPath, ResourcesUtil.getSubDir(filename));
        if (!subDir.exists()) {
            if (!subDir.mkdir()) {
                log.warn(subDir.getAbsolutePath() + "dir created failed");
            } else {
                log.info(subDir.getAbsolutePath() + "dir created");
            }
        }

        return new File(subDir.getAbsolutePath(), filename);
    }

}
