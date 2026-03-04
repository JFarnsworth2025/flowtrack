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

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.valueOf(request.getRole()));
        User savedUser = userRepository.save(user);

        Workspace personalWorkspace = Workspace.builder().name(savedUser.getUsername() + "'s Personal Workspace").type(WorkspaceType.PERSONAL).createdAt(LocalDateTime.now()).build();
        Workspace savedWorkspace = workspaceRepository.save(personalWorkspace);
        WorkspaceMember membership = WorkspaceMember.builder().workspace(savedWorkspace).user(savedUser).role(WorkspaceRole.OWNER).joinedAt(LocalDateTime.now()).build();
        workspaceMemberRepository.save(membership);

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