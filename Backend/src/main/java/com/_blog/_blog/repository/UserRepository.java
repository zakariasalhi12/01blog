package com._blog._blog.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com._blog._blog.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}