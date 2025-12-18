package com._blog._blog.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import com._blog._blog.exception.NotificationException;
import com._blog._blog.models.Notifications;
import com._blog._blog.models.User;
import com._blog._blog.repository.NotificationRepository;
import com._blog._blog.repository.UserRepository;

@Service
public class NotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserRepository userRepository;

    public void createNotification(long notifiedUserId, String message) {
        String Username = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUsername(Username).orElseThrow(() -> new NotificationException("Unauthorized", HttpStatus.UNAUTHORIZED));
        User notifiedUser = userRepository.findById(notifiedUserId).orElseThrow(() -> new NotificationException("User not found", HttpStatus.NOT_FOUND));

        Notifications notification = new Notifications(currentUser, notifiedUser, message);
        notificationRepository.save(notification);
    }

    public void markNotificationAsSeen(long notificationId) {
        String Username = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUsername(Username).orElseThrow(() -> new NotificationException("Unauthorized", HttpStatus.UNAUTHORIZED));
        Notifications notification = notificationRepository.findById(notificationId).orElseThrow(() -> new NotificationException("Notification not found", HttpStatus.NOT_FOUND));

        notification.setSeen(true);
        notificationRepository.save(notification);
    }

    public ResponseEntity<?> getNotifications(@RequestParam int page, @RequestParam int size) {

        // Get current user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);
        if (currentUser == null) {
            ResponseEntity.status(401).body("Unauthorized");
        }

        // Pagination
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notifications> notificationsPage = notificationRepository.findByNotified(currentUser, pageable);
        List<Notifications> notifications = notificationsPage.getContent();

        System.out.println(notifications.toString());

        // Prepare custom response
        List<Map<String, Object>> notifList = notifications.stream().map(n -> {
            Map<String, Object> notifMap = new HashMap<>();
            notifMap.put("notificationId", n.getId());
            notifMap.put("message", n.getMessage());
            notifMap.put("seen", n.getSeen());
            notifMap.put("createdAt", n.getCreatedAt());

            // User info of the author who triggered the notification
            User user = n.getUser();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("userId", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("avatar", user.getAvatar());
            notifMap.put("user", userMap);
            return notifMap;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("notifications", notifList);
        response.put("page", page);
        response.put("size", size);
        response.put("total", notificationsPage.getTotalElements());

        notifications.forEach(n -> {
            if (!n.getSeen()) {
                n.setSeen(true);
            }
        });
        notificationRepository.saveAll(notifications);

        return ResponseEntity.ok(response);
    }

    public void deleteNotification(long notificationId) {
        String Username = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUsername(Username).orElseThrow(() -> new NotificationException("Unauthorized", HttpStatus.UNAUTHORIZED));
        Notifications notification = notificationRepository.findById(notificationId).orElseThrow(() -> new NotificationException("Notification not found", HttpStatus.NOT_FOUND));

        notificationRepository.delete(notification);
        notificationRepository.save(notification);
    }
}
