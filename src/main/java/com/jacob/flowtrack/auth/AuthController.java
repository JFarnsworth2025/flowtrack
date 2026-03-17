package com.jacob.flowtrack.auth;

import com.jacob.flowtrack.member.Role;
import com.jacob.flowtrack.member.User;
import com.jacob.flowtrack.member.WorkspaceMember;
import com.jacob.flowtrack.member.WorkspaceMemberRepository;
import com.jacob.flowtrack.security.CustomUserDetails;
import com.jacob.flowtrack.workspace.*;
import com.jacob.flowtrack.common.ApiResponse;
import com.jacob.flowtrack.security.jwt.JwtService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder().email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).firstName(request.getFirstName()).lastName(request.getLastName()).role(Role.USER).createdAt(LocalDateTime.now()).build();
        userRepository.save(user);

        Workspace workspace = Workspace.builder().name(user.getFullName() + "'s Personal Workspace").createdAt(LocalDateTime.now()).build();
        workspaceRepository.save(workspace);

        WorkspaceMember member = WorkspaceMember.builder().workspace(workspace).user(user).role(WorkspaceRole.OWNER).joinedAt(LocalDateTime.now()).build();
        workspaceMemberRepository.save(member);
        String token = jwtService.generateToken(user.getEmail());

        return ApiResponse.success(new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtService.generateToken(loginRequest.getEmail());
        AuthResponse response = new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> getCurrentUser(@AuthenticationPrincipal CustomUserDetails details) {
        User user = details.getUser();
        AuthResponse response = new AuthResponse(null, user.getId(), user.getEmail(), user.getFullName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/test")
    public String test() {
        return "You are now authenticated.";
    }

}