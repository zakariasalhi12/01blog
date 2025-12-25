package com._blog._blog.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com._blog._blog.service.NotificationService;



@RestController
@RequestMapping("/api/")
public class NotifController {

    @Autowired
    NotificationService notificationService;
    
    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications(
        @RequestParam(defaultValue = "0") int page, 
        @RequestParam(defaultValue = "20") int size
    ) {
        return notificationService.getNotifications(page, size);
    }

    @GetMapping("/notifications/check")
    public ResponseEntity<?> checkForNotification() {
        return notificationService.checkForNotification();
    }

    @GetMapping("/notifications/seen")
    public ResponseEntity<?> markNotificationAsSeen(@RequestParam long notificationId) {
        return notificationService.markNotificationAsSeen(notificationId);
    }
    
    
}
