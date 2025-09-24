package com._blog._blog.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com._blog._blog.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // User findByUsername(String username); 
}