package com.wext.wextservice.repository;

import com.wext.wextservice.domain.Wext;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface WextRepository extends JpaRepository<Wext, String> {
    Optional<Wext> findById(String id);

    List<Wext> findAllByIdIn(List<String> ids);

    Page<Wext> findAllByUserIdOrderByCreatedTimeDesc(Long userId, Pageable pageable);

    Page<Wext> findAllByFullPathOrderByCreatedTimeDesc(String fullPath, Pageable pageable);

    Page<Wext> findAllByFullPathAndCreatedTimeLessThanEqualOrderByCreatedTimeDesc(String fullPath, Date date, Pageable pageable);

    Page<Wext> findAllByUserIdAndCreatedTimeLessThanEqualOrderByCreatedTimeDesc(Long userId, Date date, Pageable pageable);

    Page<Wext> findAllByFullPathIsStartingWithOrderByCreatedTimeDesc(String prePath, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Wext wext SET wext.repostCount = wext.repostCount + 1 WHERE wext.id = ?1")
    void incrRepostCount(@NonNull String wextID);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Wext wext SET wext.repostCount = wext.repostCount - 1 WHERE wext.id = ?1")
    void decrRepostCount(@NonNull String wextID);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Wext wext SET wext.likeCount = wext.likeCount + 1 WHERE wext.id = ?1")
    void incrLikeCount(@NonNull String wextID);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Wext wext SET wext.likeCount = wext.likeCount - 1 WHERE wext.id = ?1")
    void decrLikeCount(@NonNull String wextID);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Wext r where r.id = ?1")
    void deleteById(String id);

}
