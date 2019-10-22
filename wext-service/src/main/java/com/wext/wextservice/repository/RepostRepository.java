package com.wext.wextservice.repository;

import com.wext.wextservice.domain.Repost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RepostRepository extends JpaRepository<Repost, Long> {
    Optional<Repost> findById(Long id);
    Optional<Repost> findTopByUserIdAndWextId(Long userID, String wextID);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Repost r where r.id = ?1")
    void deleteById(Long id);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Repost r where r.userId = ?1 and r.wextId = ?2")
    void deleteByUserIdAndWextId(Long userID, String wextID);

    Page<Repost> findAllByUserIdOrderByCreatedTimeDesc(Long userId, Pageable pageable);

    Page<Repost> findAllByWextIdOrderByCreatedTimeDesc(String wextId, Pageable pageable);

    int countAllByWextId(String wextID);

    Boolean existsByUserIdAndWextId(Long userId, String wextId);
}
