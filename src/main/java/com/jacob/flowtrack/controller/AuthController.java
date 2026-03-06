package com.jacob.flowtrack.controller;

import com.jacob.flowtrack.dto.LoginRequest;
import com.jacob.flowtrack.dto.LoginResponse;
import com.jacob.flowtrack.dto.RegisterRequest;
import com.jacob.flowtrack.entity.*;
import com.jacob.flowtrack.repository.UserRepository;
import com.jacob.flowtrack.repository.WorkspaceMemberRepository;
import com.jacob.flowtrack.repository.WorkspaceRepository;
import com.jacob.flowtrack.response.ApiResponse;
import com.jacob.flowtrack.security.jwt.JwtService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    @Transactional
    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest request) {

        User user = User.builder().username(request.getUsername()).password(passwordEncoder.encode(request.getPassword())).role(Role.USER).build();
        userRepository.save(user);

        Workspace workspace = Workspace.builder().name(user.getUsername() + "'s Personal Workspace").createdAt(LocalDateTime.now()).build();
        workspaceRepository.save(workspace);

        WorkspaceMember member = WorkspaceMember.builder().workspace(workspace).user(user).role(WorkspaceRole.OWNER).joinedAt(LocalDateTime.now()).build();
        workspaceMemberRepository.save(member);

        return "User registered successfully";
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        String token = jwtService.generateToken(loginRequest.getUsername());

        return ResponseEntity.ok(ApiResponse.success("Login successful", new LoginResponse(token)));
    }

    @GetMapping("/test")
    public String test() {
        return "You are now authenticated.";
    }

}