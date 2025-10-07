package com.liang.blog.service;

import com.liang.blog.dto.user.AuthResponse;
import com.liang.blog.dto.user.LoginRequest;
import com.liang.blog.dto.user.RegisterRequest;
import com.liang.blog.dto.user.UserProfileResponse;
import com.liang.blog.dto.user.UserProfileUpdateRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserProfileResponse getProfile(Long userId);

    UserProfileResponse updateProfile(Long userId, UserProfileUpdateRequest request);
}
