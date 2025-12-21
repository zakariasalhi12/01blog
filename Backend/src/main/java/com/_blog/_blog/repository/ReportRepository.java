package com._blog._blog.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com._blog._blog.models.Comment;
import com._blog._blog.models.Like;
import com._blog._blog.models.Post;
import com._blog._blog.models.Report;
import com._blog._blog.models.User;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long>{
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    boolean existsByUserIdAndReportedId(Long userId, Long reportedId);
    Optional<Report> findByUserIdAndPostId(Long userId, Long postId);
    Page<Report> findByUserId(Long userId, Pageable pageable);
    Page<Report> findAll(Pageable pageable);
    Optional<Report> findFirstByUserIdAndPostId(Long userId, Long postId);
}
