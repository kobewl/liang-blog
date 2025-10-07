package com.liang.blog.security;

import com.liang.blog.entity.User;
import com.liang.blog.entity.enums.UserStatus;
import com.liang.blog.mapper.UserMapper;
import com.liang.blog.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserMapper userMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMapper = userMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            resolveToken(request)
                    .flatMap(jwtTokenProvider::parseUserId)
                    .flatMap(this::loadUser)
                    .ifPresent(RequestUserContext::setUser);
            filterChain.doFilter(request, response);
        } finally {
            RequestUserContext.clear();
        }
    }

    private Optional<String> resolveToken(HttpServletRequest request) {
        String header = request.getHeader(AUTH_HEADER);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return Optional.of(header.substring(BEARER_PREFIX.length()));
        }
        return Optional.empty();
    }

    private Optional<AuthenticatedUser> loadUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getStatus() != UserStatus.ACTIVE) {
            return Optional.empty();
        }
        return Optional.of(new AuthenticatedUser(
                user.getId(), user.getAccount(), user.getEmail(), user.getDisplayName()));
    }
}
