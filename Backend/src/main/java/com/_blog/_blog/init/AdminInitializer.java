package com._blog._blog.init;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com._blog._blog.models.User;
import com._blog._blog.repository.UserRepository;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            User admin = new User(
                adminUsername,
                adminEmail,
                passwordEncoder.encode(adminPassword),
                18
            );
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
        }
    }
}
