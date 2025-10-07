package com.liang.blog.security;

public final class RequestUserContext {

    private static final ThreadLocal<AuthenticatedUser> HOLDER = new ThreadLocal<>();

    private RequestUserContext() {}

    public static void setUser(AuthenticatedUser user) {
        HOLDER.set(user);
    }

    public static AuthenticatedUser getUser() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
