package com._blog._blog.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com._blog._blog.models.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long>{
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    boolean existsByUserIdAndReportedId(Long userId, Long reportedId);
    Optional<Report> findByUserIdAndPostId(Long userId, Long postId);
    Page<Report> findByUserId(Long userId, Pageable pageable);
    @SuppressWarnings("null")
    @Override
    Page<Report> findAll(Pageable pageable);
    Optional<Report> findFirstByUserIdAndPostId(Long userId, Long postId);
    Optional<Report> findFirstByUserIdAndReportedId(Long userId, Long reportedId);
}
