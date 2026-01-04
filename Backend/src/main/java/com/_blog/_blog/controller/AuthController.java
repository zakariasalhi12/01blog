package com._blog._blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._blog._blog.service.LoginService;
import com._blog._blog.service.RegisterService;
    
import jakarta.validation.Valid;

import com._blog._blog.dto.LoginRequest;
import com._blog._blog.dto.UserRequest;
import com._blog._blog.service.LoggedService;
import com._blog._blog.service.LogoutService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return loginService.login(loginRequest);
    }

    @Autowired
    private RegisterService registerService;

    @PostMapping("/signup")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequest userRequest) {
        return registerService.signup(userRequest);
    }

    @Autowired
    private LogoutService logoutService;

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        return logoutService.logout(authHeader);
    }


    @Autowired
    private LoggedService loggedService;

    @GetMapping("/logged")
    public ResponseEntity<?> checkLoggedIn() {
        return loggedService.checkLoggedIn();
    }
}
