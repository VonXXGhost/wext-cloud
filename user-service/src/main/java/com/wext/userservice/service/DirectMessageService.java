package com.wext.userservice.service;

import com.wext.common.domain.exception.InvalidOperationException;
import com.wext.userservice.domain.DirectMessage;
import com.wext.userservice.repository.DirectMessageRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Service
@Slf4j
public class DirectMessageService {
    private DirectMessageRepository dmRepository;

    @Autowired
    public DirectMessageService(DirectMessageRepository dmRepository) {
        this.dmRepository = dmRepository;
    }

    public List<DirectMessage> getUserAllDM(@NonNull Long userId, Integer page) {
        if (page == null) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, 30);

        Page<DirectMessage> dms = dmRepository
                .findAllByUserIdFromOrUserIdToOrderByCreatedTimeDesc(userId, userId, pageable);
        log.debug(dms.getContent().toString());
        return dms.getContent();
    }

    public Map<String, Object> getUserSendDM(@NonNull Long userId, Integer page) {
        if (page == null) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, 30);

        Page<DirectMessage> dms = dmRepository
                .findAllByUserIdFromOrderByCreatedTimeDesc(userId, pageable);

        Map<String, Object> res = new TreeMap<>();
        res.put("total_pages", dms.getTotalPages());
        res.put("dms", dms.getContent());
        log.debug(res.toString());
        return res;
    }

    public Map<String, Object> getUserReceiveDM(@NonNull Long userId, Integer page) {
        if (page == null) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, 30);

        Page<DirectMessage> dms = dmRepository
                .findAllByUserIdToOrderByCreatedTimeDesc(userId, pageable);

        Map<String, Object> res = new TreeMap<>();
        res.put("total_pages", dms.getTotalPages());
        res.put("dms", dms.getContent());
        log.debug(res.toString());
        return res;
    }

    public DirectMessage createNewDM(@NonNull Long userFrom, @NonNull Long userTo, @NonNull String content) {
        if (content.length() > 1000) {
            throw new InvalidOperationException("Message is too long");
        }
        DirectMessage dm = DirectMessage.builder()
                .userIdFrom(userFrom)
                .userIdTo(userTo)
                .content(content)
                .haveRead(false)
                .build();

        log.info("New message: " + dm);
        return dmRepository.save(dm);
    }

    public void readDM(@NonNull List<Long> dmIds, @NonNull Long userId) {
        for (Long id : dmIds) {
            Optional<DirectMessage> find = dmRepository.findById(id);
            if (!find.isPresent()) {
                continue;   // 不存在的私信忽略跳过，不需要报错
            }
            DirectMessage dm = find.get();
            if (!dm.getUserIdTo().equals(userId)) {
                continue;   // 不是发给自己的也跳过，不报错
            }
            dm.setHaveRead(true);
            dmRepository.save(dm);
            log.info("Have read: " + dm);
        }
    }

    public void deleteDM(@NonNull Long id) {
        log.info("Delete message: " + id);

        dmRepository.deleteById(id);
    }

    public DirectMessage getDM(@NonNull Long id) {
        return dmRepository.findById(id).orElseThrow(() -> new InvalidOperationException("The message is non-existent."));
    }
}
