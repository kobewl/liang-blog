package com.liang.blog.controller;

import com.liang.blog.dto.common.ApiResponse;
import com.liang.blog.dto.user.AuthResponse;
import com.liang.blog.dto.user.LoginRequest;
import com.liang.blog.dto.user.RegisterRequest;
import com.liang.blog.dto.user.UserProfileResponse;
import com.liang.blog.dto.user.UserProfileUpdateRequest;
import com.liang.blog.security.AuthenticatedUser;
import com.liang.blog.security.CurrentUser;
import com.liang.blog.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> me(@CurrentUser AuthenticatedUser currentUser) {
        UserProfileResponse profile = authService.getProfile(currentUser.getId());
        return ApiResponse.success(profile);
    }

    @PutMapping("/me")
    public ApiResponse<UserProfileResponse> updateProfile(
            @CurrentUser AuthenticatedUser currentUser,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        UserProfileResponse profile = authService.updateProfile(currentUser.getId(), request);
        return ApiResponse.success(profile);
    }
}
