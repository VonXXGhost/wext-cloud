package com.wext.wextservice.repository;

import com.wext.wextservice.domain.PathCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface PathCountRepository extends JpaRepository<PathCount, String> {

    Page<PathCount> getHotChildPath(@Param("parentPath") String parentPath, @Param("hours") int hours, Pageable pageable);

}
