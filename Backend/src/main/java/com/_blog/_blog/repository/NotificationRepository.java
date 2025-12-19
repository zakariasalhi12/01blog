package com._blog._blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com._blog._blog.models.Notifications;
import com._blog._blog.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, Long>{
    Page<Notifications> findByUser(User user ,Pageable pageable);
    Page<Notifications> findByNotified(User user ,Pageable pageable);
    boolean existsByNotifiedAndSeenFalse(User user);
}
