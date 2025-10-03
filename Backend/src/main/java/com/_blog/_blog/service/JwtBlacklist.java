package com._blog._blog.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com._blog._blog.models.Blacklist;
import com._blog._blog.repository.BlacklistRepository;

@Service
@Component
public class JwtBlacklist {

    private final BlacklistRepository repository;

    public JwtBlacklist(BlacklistRepository repository) {
        this.repository = repository;
    }

    // Add a token to the blacklist
    public void addToken(String token, LocalDateTime expiresAt) {
        Blacklist entry = new Blacklist(token, expiresAt);
        repository.save(entry);
    }

    // Check if token is blacklisted
    public boolean isBlacklisted(String token) {
        return repository.findByToken(token).isPresent();
    }

    // Remove all expired tokens
    public void removeExpiredTokens() {
        repository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
