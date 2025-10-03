package com._blog._blog.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com._blog._blog.models.Blacklist;

public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {
    Optional<Blacklist> findByToken(String token);
    void deleteByExpiresAtBefore(LocalDateTime now);
}