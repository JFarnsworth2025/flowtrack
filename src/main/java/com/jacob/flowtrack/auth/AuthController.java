package com.jacob.flowtrack.auth;

import com.jacob.flowtrack.member.Role;
import com.jacob.flowtrack.member.User;
import com.jacob.flowtrack.member.WorkspaceMember;
import com.jacob.flowtrack.member.WorkspaceMemberRepository;
import com.jacob.flowtrack.workspace.*;
import com.jacob.flowtrack.common.ApiResponse;
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

        User user = User.builder().email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).role(Role.USER).build();
        userRepository.save(user);

        Workspace workspace = Workspace.builder().name(user.getFullName() + "'s Personal Workspace").createdAt(LocalDateTime.now()).build();
        workspaceRepository.save(workspace);

        WorkspaceMember member = WorkspaceMember.builder().workspace(workspace).user(user).role(WorkspaceRole.OWNER).joinedAt(LocalDateTime.now()).build();
        workspaceMemberRepository.save(member);

        return "User registered successfully";
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        String token = jwtService.generateToken(loginRequest.getEmail());

        return ResponseEntity.ok(ApiResponse.success(token));
    }

    @GetMapping("/test")
    public String test() {
        return "You are now authenticated.";
    }

}