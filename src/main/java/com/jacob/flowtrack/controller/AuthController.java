package com.jacob.flowtrack.controller;

import com.jacob.flowtrack.dto.RegisterRequest;
import com.jacob.flowtrack.entity.Role;
import com.jacob.flowtrack.entity.User;
import com.jacob.flowtrack.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.valueOf(request.getRole()));
        userRepository.save(user);
        return "User registered successfully";
    }

    @GetMapping("/test")
    public String test() {
        return "You are now authenticated.";
    }

}