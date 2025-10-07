package com.liang.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liang.blog.dto.user.AuthResponse;
import com.liang.blog.dto.user.LoginRequest;
import com.liang.blog.dto.user.RegisterRequest;
import com.liang.blog.dto.user.UserProfileResponse;
import com.liang.blog.dto.user.UserProfileUpdateRequest;
import com.liang.blog.entity.User;
import com.liang.blog.entity.enums.UserStatus;
import com.liang.blog.mapper.UserMapper;
import com.liang.blog.service.AuthService;
import com.liang.blog.util.JwtTokenProvider;
import com.liang.blog.util.PasswordHasher;
import com.liang.blog.util.TimeProvider;
import com.liang.blog.util.UserResponseMapper;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordHasher passwordHasher;
    private final JwtTokenProvider jwtTokenProvider;
    private final TimeProvider timeProvider;

    public AuthServiceImpl(
            UserMapper userMapper,
            PasswordHasher passwordHasher,
            JwtTokenProvider jwtTokenProvider,
            TimeProvider timeProvider) {
        this.userMapper = userMapper;
        this.passwordHasher = passwordHasher;
        this.jwtTokenProvider = jwtTokenProvider;
        this.timeProvider = timeProvider;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        ensureAccountUnique(request.getAccount(), request.getEmail());
        LocalDateTime now = timeProvider.now();
        User user = new User();
        user.setAccount(request.getAccount());
        user.setEmail(request.getEmail());
        user.setDisplayName(request.getDisplayName());
        user.setPasswordHash(passwordHasher.hash(request.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userMapper.insert(user);
        String token = jwtTokenProvider.generateToken(user.getId());
        return new AuthResponse(token, UserResponseMapper.toProfile(user));
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = findByAccountOrEmail(request.getAccountOrEmail());
        if (user == null || user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("账号不存在或已被禁用");
        }
        if (!passwordHasher.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("账号或密码错误");
        }
        LocalDateTime now = timeProvider.now();
        user.setLastLoginAt(now);
        user.setUpdatedAt(now);
        userMapper.updateById(user);
        String token = jwtTokenProvider.generateToken(user.getId());
        return new AuthResponse(token, UserResponseMapper.toProfile(user));
    }

    @Override
    public UserProfileResponse getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("用户不存在或已被禁用");
        }
        return UserResponseMapper.toProfile(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("用户不存在或已被禁用");
        }
        if (request.getDisplayName() != null) {
            String value = request.getDisplayName().trim();
            if (value.isEmpty()) {
                throw new IllegalArgumentException("昵称不能为空");
            }
            user.setDisplayName(value);
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(StringUtils.hasText(request.getAvatarUrl()) ? request.getAvatarUrl().trim() : null);
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio().trim());
        }
        user.setUpdatedAt(timeProvider.now());
        userMapper.updateById(user);
        return UserResponseMapper.toProfile(user);
    }

    private void ensureAccountUnique(String account, String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getAccount, account).or().eq(User::getEmail, email);
        wrapper.last("LIMIT 1");
        User existing = userMapper.selectOne(wrapper);
        if (existing != null) {
            if (existing.getAccount().equalsIgnoreCase(account)) {
                throw new IllegalArgumentException("账号已被注册");
            }
            if (existing.getEmail().equalsIgnoreCase(email)) {
                throw new IllegalArgumentException("邮箱已被注册");
            }
            throw new IllegalArgumentException("账号或邮箱已存在");
        }
    }

    private User findByAccountOrEmail(String accountOrEmail) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getAccount, accountOrEmail).or().eq(User::getEmail, accountOrEmail);
        wrapper.last("LIMIT 1");
        return userMapper.selectOne(wrapper);
    }
}
