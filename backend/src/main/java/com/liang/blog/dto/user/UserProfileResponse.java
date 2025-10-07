package com.liang.blog.dto.user;

import com.liang.blog.entity.enums.UserStatus;
import java.time.LocalDateTime;

public class UserProfileResponse {

    private final Long id;
    private final String account;
    private final String email;
    private final String displayName;
    private final String avatarUrl;
    private final String bio;
    private final UserStatus status;
    private final LocalDateTime createdAt;

    public UserProfileResponse(
            Long id,
            String account,
            String email,
            String displayName,
            String avatarUrl,
            String bio,
            UserStatus status,
            LocalDateTime createdAt) {
        this.id = id;
        this.account = account;
        this.email = email;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public UserStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
