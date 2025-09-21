package com._blog._blog;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Simplehome {
    @GetMapping("/")
    public String home() {
        return "<h1>hello world<h1>";
    }
}
