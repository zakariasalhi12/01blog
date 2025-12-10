package com._blog._blog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com._blog._blog.models.Comment;
import com._blog._blog.models.Like;
import com._blog._blog.models.Post;
import com._blog._blog.models.User;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndComment(User user, Comment comment);
    Optional<Like> findByUserAndPost(User user, Post post);
}
