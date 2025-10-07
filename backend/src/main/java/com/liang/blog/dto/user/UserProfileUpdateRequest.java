package com.liang.blog.dto.user;

import jakarta.validation.constraints.Size;

public class UserProfileUpdateRequest {

    @Size(max = 100, message = "昵称长度不能超过100个字符")
    private String displayName;

    @Size(max = 512, message = "头像 URL 过长")
    private String avatarUrl;

    @Size(max = 2000, message = "个人简介太长")
    private String bio;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
