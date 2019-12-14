package com.wext.wextservice.service;

import com.wext.common.domain.exception.NonExistentException;
import com.wext.common.utils.WextTool;
import com.wext.wextservice.domain.Wext;
import com.wext.wextservice.repository.WextRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WextService {

    private WextRepository wextRepository;
    private static final Integer defaultPageSize = 50;

    @Autowired
    public WextService(WextRepository wextRepository) {
        this.wextRepository = wextRepository;
    }

    public Wext createWext(@NonNull Long userID, @NonNull String text,
                           String fullPath, List<String> pics){
        if (fullPath == null) {
            fullPath = "/";
        }
        // 存在性检查和其他业务性检查于controller进行

        String pics_s = WextTool.picsListToString(pics);
        String wext_id = WextTool.geneWextID(userID);

        Wext wext = wextRepository.saveAndFlush(Wext.builder()
                .id(wext_id)
                .fullPath(fullPath)
                .userId(userID)
                .text(text)
                .pics(pics_s)
                .commentCount(0L)
                .likeCount(0L)
                .repostCount(0L)
                .build()
        );
        // 注意：此时的时间戳带毫秒，需要重新转换
//        String format = "yyyy-MM-dd HH:mm:ss zzz";
//        SimpleDateFormat sdf = new SimpleDateFormat(format);
//        String time = sdf.format(wext.getCreatedTime());
//        try {
//            wext.setCreatedTime(sdf.parse(time));
//        } catch (ParseException e) {
//            log.error(e.getMessage());
//        }
        log.info("New wext: " + wext);
        return wext;
    }

    public Wext getWext(@NonNull String wextID) {
        Wext wext = wextRepository.findById(wextID)
                .orElseThrow(() -> new NonExistentException("Can not find the wext."));
        log.debug(wext.toString());
        return wext;
    }

    public Map<String, Wext> getWexts(@NonNull Collection<String> wextIDs) {
        log.debug(wextIDs.toString());
        var wexts = wextRepository.findAllByIdIn(wextIDs);
        Map<String, Wext> resultMap = new HashMap<>(wexts.size());
        wexts.forEach(wext -> resultMap.put(wext.getId(), wext));
        log.debug(wexts.toString());
        return resultMap;
    }

    public List<Wext> getWextsOfPath(@NonNull String fullPath,
                                     Integer page, Integer pageSize) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = defaultPageSize;
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        List<Wext> result = wextRepository.findAllByFullPathOrderByCreatedTimeDesc(fullPath, pageable).getContent();
        log.debug(result.toString());
        return result;
    }

    public List<Wext> getWextsOfUser(@NonNull Long userID,
                                     Integer page, Integer pageSize) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = defaultPageSize;
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        List<Wext> result = wextRepository.findAllByUserIdOrderByCreatedTimeDesc(userID, pageable).getContent();
        log.debug(result.toString());
        return result;
    }

    public List<Wext> wextsFilter(List<Wext> resultBase, String lastWextID) {
        int pos = 0;
        for (Wext r : resultBase) { // 获得last在查询结果中的位置
            if (r.getId().equals(lastWextID)) {
                break;
            } else {
                pos++;
            }
        }
        if (resultBase.size() == pos) { // last不存在于查询结果中
            return resultBase;
        }
        if (resultBase.size() == pos - 1) { // 无新条目，返回空
            resultBase.clear();
            return resultBase;
        }
        List<Wext> result = resultBase.subList(pos + 1, resultBase.size());
        log.debug("After result: " + result.toString());
        return result;
    }

    public List<String> wextsIDFilter(List<String> resultBase, String lastWextID) {
        int pos = 0;
        for (String r : resultBase) { // 获得last在查询结果中的位置
            if (r.equals(lastWextID)) {
                break;
            } else {
                pos++;
            }
        }
        if (resultBase.size() == pos) { // last不存在于查询结果中
            return resultBase;
        }
        if (resultBase.size() == pos - 1) { // 无新条目，返回空
            resultBase.clear();
            return resultBase;
        }
        List<String> result = resultBase.subList(pos + 1, resultBase.size());
        log.debug("After result: " + result.toString());
        return result;
    }

    public List<Wext> getWextsOfPath(@NonNull String fullPath, String lastWextID, Integer pageSize) {
        if (lastWextID == null) {
            return getWextsOfPath(fullPath, 1, pageSize);
        }
        Wext last = wextRepository.findById(lastWextID)
                .orElseThrow(() -> new NonExistentException("Can not find the wext."));
        // TODO id不存在时可能是中途被删除了，考虑读出时间进行获取

        if (pageSize == null || pageSize < 1) {
            pageSize = defaultPageSize;
        }
        Pageable pageable = PageRequest.of(0, pageSize);

        List<Wext> resultBase = wextRepository.findAllByFullPathAndCreatedTimeLessThanEqualOrderByCreatedTimeDesc(
                fullPath, last.getCreatedTime(), pageable
        ).getContent(); // 此时包含重复数据
        log.debug(resultBase.toString());

        return wextsFilter(resultBase, lastWextID);
    }

    public List<Wext> getWextsOfUser(@NonNull Long userID, String lastWextID, Integer pageSize) {
        if (lastWextID == null) {
            return getWextsOfUser(userID, 1, pageSize);
        }
        Wext last = wextRepository.findById(lastWextID)
                .orElseThrow(() -> new NonExistentException("Can not find the wext."));
        // TODO id不存在时可能是中途被删除了，考虑读出时间进行获取

        if (pageSize == null || pageSize < 1) {
            pageSize = defaultPageSize;
        }
        Pageable pageable = PageRequest.of(0, pageSize);

        List<Wext> resultBase = wextRepository.findAllByUserIdAndCreatedTimeLessThanEqualOrderByCreatedTimeDesc(
                userID, last.getCreatedTime(), pageable
        ).getContent(); // 此时包含重复数据
        log.debug(resultBase.toString());

        return wextsFilter(resultBase, lastWextID);
    }

    public List<Wext> getWextsByPrefix(@NonNull String prefix,
                                       Integer page, Integer pageSize) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = defaultPageSize;
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        List<Wext> result = wextRepository.findAllByFullPathIsStartingWithOrderByCreatedTimeDesc(
                prefix, pageable
        ).getContent();
        log.debug(result.toString());

        return result;
    }

    public void deleteWext(@NonNull String wextID) {
        wextRepository.deleteById(wextID);
    }
}
