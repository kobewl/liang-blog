package com.liang.blog.util;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordHasher {

    private static final int DEFAULT_STRENGTH = 10;

    public String hash(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(DEFAULT_STRENGTH));
    }

    public boolean matches(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null || hashedPassword.isBlank()) {
            return false;
        }
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}
