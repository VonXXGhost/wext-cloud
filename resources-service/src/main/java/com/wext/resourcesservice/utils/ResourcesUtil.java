package com.wext.resourcesservice.utils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
public class ResourcesUtil {
    /**
     * 获得应该分发的次文件夹名
     *
     * @param filename 资源文件名
     * @return 次文件夹名
     */
    public static String getSubDir(@NonNull String filename) {
        var split = filename.split("[_.]");
        if (split.length < 3) {
            throw new RuntimeException("Wrong filename");
        }
        var timestamp = Long.parseLong(split[1]);
        var localDate = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDate();
        return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
