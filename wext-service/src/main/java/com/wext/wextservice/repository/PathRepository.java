package com.wext.wextservice.repository;

import com.wext.wextservice.domain.Path;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PathRepository extends JpaRepository<Path, Long> {
    Page<Path> findAllByParentId(Long parentId, Pageable pageable);

    Optional<Path> findByFullPath(String fullPath);

    Page<Path> findAllByFullPathIsStartingWith(String prePath, Pageable pageable);

}
