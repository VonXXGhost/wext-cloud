package com.wext.wextservice.repository;

import com.wext.wextservice.domain.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findById(Long id);

    Optional<Like> findTopByUserIdAndWextId(Long userID, String wextID);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Like r where r.id = ?1")
    void deleteById(Long id);

    Boolean existsByUserIdAndWextId(Long userId, String wextId);

    Page<Like> findAllByWextIdOrderByCreatedTimeDesc(String wextId, Pageable pageable);

    Page<Like> findAllByUserIdOrderByCreatedTimeDesc(Long userId, Pageable pageable);
}
