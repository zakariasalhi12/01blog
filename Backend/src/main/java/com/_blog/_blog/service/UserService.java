package com._blog._blog.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com._blog._blog.model.User;
import com._blog._blog.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(String username, String email, String password, String avatar, int age) {
        User user = new User(username, email, password, age);
        return userRepository.save(user);
    }
}