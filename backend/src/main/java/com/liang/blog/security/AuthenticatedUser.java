package com.liang.blog.security;

public class AuthenticatedUser {

    private final Long id;
    private final String account;
    private final String email;
    private final String displayName;

    public AuthenticatedUser(Long id, String account, String email, String displayName) {
        this.id = id;
        this.account = account;
        this.email = email;
        this.displayName = displayName;
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
}
