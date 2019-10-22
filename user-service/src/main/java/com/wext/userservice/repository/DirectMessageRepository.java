package com.wext.userservice.repository;

import com.wext.userservice.domain.DirectMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {
    Optional<DirectMessage> findById(Long id);

    Page<DirectMessage> findAllByUserIdFromOrderByCreatedTimeDesc(Long userIdFrom, Pageable pageable);

    Page<DirectMessage> findAllByUserIdToOrderByCreatedTimeDesc(Long userIdTo, Pageable pageable);

    Page<DirectMessage>  findAllByUserIdToAndHaveReadIsFalseOrderByCreatedTimeDesc(Long userIdTo, Pageable pageable);

    Page<DirectMessage> findAllByUserIdFromOrUserIdToOrderByCreatedTimeDesc(Long userIdFrom, Long userIdTo, Pageable pageable);

    void deleteById(Long id);
}
