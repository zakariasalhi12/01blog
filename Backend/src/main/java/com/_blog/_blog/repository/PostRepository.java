package com._blog._blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com._blog._blog.models.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // Existing methods
    Page<Post> findAllByAuthorUsername(String username, Pageable pageable);
    @SuppressWarnings("null")
    @Override
    Optional<Post> findById(Long id);
    
    // Visibility filtering
    Page<Post> findAllByVisibleTrue(Pageable pageable);
    Page<Post> findAllByAuthorUsernameAndVisibleTrue(String username, Pageable pageable);
    
    // Subscribed users' posts
    Page<Post> findAllByAuthorIdInAndVisibleTrue(List<Long> authorIds, Pageable pageable);
}
