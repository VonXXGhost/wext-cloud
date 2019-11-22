package com.wext.common.utils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
public class WextTool {

    public static String geneWextID(@NonNull Long userID) {
        int length = userID.toString().length();
        String timestamp = String.valueOf(new Date().getTime());

        String sourceID = String.format("%02d", length)
                + userID
                + timestamp.substring(0, timestamp.length() - 3);
        log.info("Source wext_id: " + sourceID);

        return Base64.getUrlEncoder().encodeToString(sourceID.getBytes(StandardCharsets.US_ASCII));
    }

    public static String picsListToString(List<String> pics) {
        if (pics == null || pics.size() == 0) {
            return "";
        }
        StringBuilder s = new StringBuilder();
        for (String pic : pics) {
            s.append(pic).append("\n");
        }
        log.debug(s.toString());
        return s.toString();
    }

    public static List<String> stringToPicsList(String s) {
        if (s == null || s.equals("")) {
            return new ArrayList<>();
        }
        return Arrays.asList(s.split("\n"));
    }

    public static List<String> splitPath(@NonNull String fullPath) {
        List<String> result = new ArrayList<>();
        result.add("/");
        if (fullPath.equals("/")) { // charAt会忽略根节点和全节点
            return result;
        }

        IntStream.range(1, fullPath.length())
                .filter(i -> fullPath.charAt(i) == '/')
                .forEach(i -> result.add(fullPath.substring(0, i)));

        result.add(fullPath);
        return result;
    }

}
