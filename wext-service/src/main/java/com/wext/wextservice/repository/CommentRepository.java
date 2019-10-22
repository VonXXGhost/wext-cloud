package com.wext.wextservice.repository;

import com.wext.wextservice.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findById(Long id);

    Page<Comment> findAllByWextIdOrderByFloorAsc(String wextId, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Comment r where r.id = ?1")
    void deleteById(Long id);
}
