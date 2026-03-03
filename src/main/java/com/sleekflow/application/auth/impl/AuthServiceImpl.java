package com.sleekflow.application.auth.impl;

import com.sleekflow.application.auth.IAuthService;
import com.sleekflow.domain.user.User;
import com.sleekflow.infrastructure.config.JwtProperties;
import com.sleekflow.infrastructure.exception.DuplicateResourceException;
import com.sleekflow.infrastructure.exception.ResourceNotFoundException;
import com.sleekflow.infrastructure.persistence.user.UserRepository;
import com.sleekflow.infrastructure.security.JwtTokenProvider;
import com.sleekflow.interfaces.assembler.UserAssembler;
import com.sleekflow.interfaces.dto.request.LoginRequest;
import com.sleekflow.interfaces.dto.request.RegisterRequest;
import com.sleekflow.interfaces.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证服务实现类
 * <p>
 * Authentication Service Implementation
 * </p>
 * <p>
 * 提供用户注册和登录的业务逻辑实现。
 * </p>
 * <p>
 * Implements business logic for user registration and login.
 * </p>
 * <p>
 * <b>主要功能（Key Features）：</b></p>
 * <ul>
 *   <li>用户注册：检查邮箱唯一性、密码加密、生成 JWT 令牌 / User registration: email uniqueness check, password encryption, JWT generation</li>
 *   <li>用户登录：邮箱查找、密码验证、生成 JWT 令牌 / User login: email lookup, password verification, JWT generation</li>
 *   <li>使用 BCrypt 算法进行密码加密 / Uses BCrypt algorithm for password encryption</li>
 *   <li>JWT 令牌有效期 24 小时 / JWT token valid for 24 hours</li>
 * </ul>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final UserAssembler userAssembler;

    /**
     * 用户注册
     * <p>
     * User registration
     * </p>
     * <p>
     * 创建新用户账户。邮箱地址必须唯一，密码将使用 BCrypt 算法加密后存储。
     * 注册成功后自动生成 JWT 令牌并返回。
     * </p>
     * <p>
     * Creates a new user account. Email address must be unique, password will be encrypted using BCrypt.
     * JWT token is automatically generated and returned upon successful registration.
     * </p>
     *
     * @param request 注册请求 / Registration request containing email, username, and password
     * @return 认证响应，包含 JWT 令牌和用户信息 / Authentication response containing JWT token and user info
     * @throws DuplicateResourceException 如果邮箱已被注册 / if email is already registered
     */
    @Override
    public AuthResponse register(RegisterRequest request) {
        // 检查邮箱是否已存在 / Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        // 创建用户，密码使用 BCrypt 加密 / Create user with BCrypt encrypted password
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        // 保存用户并生成认证响应 / Save user and build auth response
        user = userRepository.save(user);
        return buildAuthResponse(user);
    }

    /**
     * 用户登录
     * <p>
     * User login
     * </p>
     * <p>
     * 使用邮箱和密码进行身份验证。
     * 验证成功后返回 JWT 令牌，有效期 24 小时。
     * </p>
     * <p>
     * Authenticates user using email and password.
     * Returns a JWT token with 24-hour validity upon successful authentication.
     * </p>
     *
     * @param request 登录请求 / Login request containing email and password
     * @return 认证响应，包含 JWT 令牌和用户信息 / Authentication response containing JWT token and user info
     * @throws ResourceNotFoundException 如果用户不存在 / if user does not exist
     * @throws BadCredentialsException 如果密码错误 / if password is incorrect
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        // 根据邮箱查找用户 / Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        // 验证密码 / Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // 生成认证响应 / Build auth response
        return buildAuthResponse(user);
    }

    /**
     * 构建认证响应
     * <p>
     * Build authentication response
     * </p>
     * <p>
     * 为指定用户生成 JWT 令牌并构建认证响应对象。
     * </p>
     * <p>
     * Generates JWT token for the specified user and builds authentication response object.
     * </p>
     *
     * @param user 用户实体 / User entity
     * @return 认证响应 / Authentication response
     */
    private AuthResponse buildAuthResponse(User user) {
        // 生成 JWT 令牌 / Generate JWT token
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        // 计算令牌有效期（秒）/ Calculate token expiration in seconds
        long expiresIn = jwtProperties.getExpirationMs() / 1000;

        // 构建并返回认证响应 / Build and return auth response
        return new AuthResponse(token, "Bearer", expiresIn, userAssembler.toSummary(user));
    }
}
