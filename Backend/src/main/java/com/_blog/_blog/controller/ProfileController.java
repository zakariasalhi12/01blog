package com._blog._blog.controller;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._blog._blog.service.SubscribeService;

@RestController
@RequestMapping("/api")
public class ProfileController {

    @Autowired
    private SubscribeService subscribeService;

    @GetMapping("/profile/subscribe")
    public ResponseEntity<?> subscribe(@RequestParam("id") Long subscribedToId) {
        return subscribeService.subscribe(subscribedToId);
    }
}
