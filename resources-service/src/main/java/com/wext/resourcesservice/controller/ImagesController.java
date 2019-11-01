package com.wext.resourcesservice.controller;

import com.wext.resourcesservice.utils.ResourcesUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/images")
public class ImagesController {

    @Value("${upload.img-absolute-path}")
    private String absoluteImgPath;

    private static final Map<String, MediaType> mineTable = Map.of(
            "png", MediaType.IMAGE_PNG,
            "jpg", MediaType.IMAGE_JPEG,
            "gif", MediaType.IMAGE_GIF
    );

    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        var suffix = filename.substring(filename.lastIndexOf('.') + 1);
        var subDir = ResourcesUtil.getSubDir(filename);
        var file = new File(new File(absoluteImgPath, subDir), filename);

        try {
            var pic = new FileInputStream(file);
            return ResponseEntity.ok()
                    .contentType(mineTable.get(suffix))
                    .body(pic.readAllBytes());
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }
}
