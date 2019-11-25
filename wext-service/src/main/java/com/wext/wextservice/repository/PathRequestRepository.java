package com.wext.wextservice.repository;

import com.wext.wextservice.domain.PathRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PathRequestRepository extends JpaRepository<PathRequest, Long> {
    Page<PathRequest> findAllByState(String state, Pageable pageable);

    Page<PathRequest> findAllByApplicantID(Long applicantID, Pageable pageable);

    Page<PathRequest> findAllByApplicantIDAndState(Long applicantID, String state, Pageable pageable);

    Optional<PathRequest> findByFullPath(String fullPath);

}
