package com._blog._blog.filters;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Lightweight per-IP fixed-window rate limiter.
 * Limits each client IP to 150 requests per second (fixed window).
 * Implemented in-memory to avoid external dependency issues.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitFilter implements Filter {

    private static class IpRecord {
        volatile long windowStartSec;
        int count;
        volatile long lastSeenMillis;

        IpRecord(long nowSec) {
            this.windowStartSec = nowSec;
            this.count = 0;
            this.lastSeenMillis = Instant.now().toEpochMilli();
        }
    }

    private final Map<String, IpRecord> map = new ConcurrentHashMap<>();

    private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "rate-limit-cleaner");
        t.setDaemon(true);
        return t;
    });

    private static final int LIMIT_PER_SECOND = 150;
    private static final long EVICT_AFTER_MILLIS = 10 * 60 * 1000L; // 10 minutes

    public RateLimitFilter() {
        cleaner.scheduleAtFixedRate(this::evictOldEntries, 60, 60, TimeUnit.SECONDS);
    }

    private void evictOldEntries() {
        long cutoff = Instant.now().toEpochMilli() - EVICT_AFTER_MILLIS;
        for (Map.Entry<String, IpRecord> e : map.entrySet()) {
            if (e.getValue().lastSeenMillis < cutoff) {
                map.remove(e.getKey());
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpResp = (HttpServletResponse) response;

        String xf = httpReq.getHeader("X-Forwarded-For");
        String clientIp;
        if (xf != null && !xf.isEmpty()) {
            clientIp = xf.split(",")[0].trim();
        } else {
            clientIp = request.getRemoteAddr();
        }

        long nowSec = Instant.now().getEpochSecond();

        IpRecord rec = map.computeIfAbsent(clientIp, k -> new IpRecord(nowSec));

        synchronized (rec) {
            rec.lastSeenMillis = Instant.now().toEpochMilli();
            if (rec.windowStartSec != nowSec) {
                rec.windowStartSec = nowSec;
                rec.count = 1;
            } else {
                rec.count++;
            }

            if (rec.count <= LIMIT_PER_SECOND) {
                chain.doFilter(request, response);
            } else {
                httpResp.setStatus(429);
                httpResp.setContentType("application/json;charset=UTF-8");
                httpResp.getWriter().write("{\"error\": \"Too Many Requests\"}");
            }
        }
    }
}
