package com._blog._blog.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com._blog._blog.models.Subscriptions;
import com._blog._blog.models.User;
import com._blog._blog.repository.SubscriptionsRepository;
import com._blog._blog.repository.UserRepository;

@Service
public class SubscribeService {

    @Autowired
    private SubscriptionsRepository subscriptionsRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<Map<String, String>> subscribe(Long subscribedToId) {

        Map<String, String> res = new HashMap<>();
        System.out.println("Subscribed to ID: " + subscribedToId);
        // Get logged user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> subscriberOpt = userRepository.findByUsername(username);

        if (subscriberOpt.isEmpty()) {
            res.put("status", "error");
            res.put("message", "User not found");
            return ResponseEntity.badRequest().body(res);
        }

        User subscriber = subscriberOpt.get();

        if (Objects.equals(subscribedToId, subscriber.getId())) {
            res.put("status", "error");
            res.put("message", "Cannot subscribe to yourself");
            return ResponseEntity.badRequest().body(res);
        }

        // get target user
        User target = userRepository.findById(subscribedToId).orElse(null);
        if (target == null) {
            res.put("status", "error");
            res.put("message", "Target user not found");
            return ResponseEntity.badRequest().body(res);
        }

        // Check if already subscribed
        List<Subscriptions> existing = subscriptionsRepository
                .findBySubscriberId_IdAndSubscribedToId_Id(subscriber.getId(), target.getId());

        if (!existing.isEmpty()) {
            // Already subscribed → Remove subscription
            subscriptionsRepository.deleteAll(existing);

            res.put("status", "unsubscribed");
            res.put("subscriberId", subscriber.getId().toString());
            res.put("subscribedToId", target.getId().toString());

            return ResponseEntity.ok(res);
        }

        // Not subscribed → Create new subscription
        Subscriptions newSub = new Subscriptions(subscriber, target);
        subscriptionsRepository.save(newSub);

        res.put("status", "subscribed");
        res.put("subscriberId", subscriber.getId().toString());
        res.put("subscribedToId", target.getId().toString());

        return ResponseEntity.ok(res);
    }
}
