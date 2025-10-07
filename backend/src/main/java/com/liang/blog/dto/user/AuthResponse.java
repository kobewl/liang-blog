package com.liang.blog.dto.user;

public class AuthResponse {

    private final String token;
    private final UserProfileResponse user;

    public AuthResponse(String token, UserProfileResponse user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public UserProfileResponse getUser() {
        return user;
    }
}
