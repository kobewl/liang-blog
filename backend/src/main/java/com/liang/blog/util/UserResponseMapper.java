package com.liang.blog.util;

import com.liang.blog.dto.user.UserProfileResponse;
import com.liang.blog.entity.User;

public final class UserResponseMapper {

    private UserResponseMapper() {}

    public static UserProfileResponse toProfile(User user) {
        if (user == null) {
            return null;
        }
        return new UserProfileResponse(
                user.getId(),
                user.getAccount(),
                user.getEmail(),
                user.getDisplayName(),
                user.getAvatarUrl(),
                user.getBio(),
                user.getStatus(),
                user.getCreatedAt());
    }
}
