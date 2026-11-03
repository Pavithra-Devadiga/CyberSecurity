package com.security.scanner.controller;

import com.security.scanner.dto.request.LoginRequest;
import com.security.scanner.dto.request.RegisterRequest;
import com.security.scanner.dto.response.ApiResponse;
import com.security.scanner.dto.response.AuthResponse;
import com.security.scanner.dto.response.UserDto;
import com.security.scanner.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        UserDto profile = authService.getCurrentUserProfile();
        return ResponseEntity.ok(ApiResponse.success(profile));
    }
}
