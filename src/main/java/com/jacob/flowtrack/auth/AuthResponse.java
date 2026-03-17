package com.jacob.flowtrack.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthResponse {

    private String token;
    private Long id;
    private String email;
    private String fullName;

}