package com.sleekflow.application.auth;

import com.sleekflow.interfaces.dto.request.LoginRequest;
import com.sleekflow.interfaces.dto.request.RegisterRequest;
import com.sleekflow.interfaces.dto.response.AuthResponse;

/**
 * 认证服务接口
 * <p>
 * Authentication Service Interface
 * </p>
 * <p>
 * 定义用户认证相关的业务操作，包括用户注册和登录。
 * </p>
 * <p>
 * Defines business operations for user authentication, including registration and login.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
public interface IAuthService {

    /**
     * 用户注册
     * <p>
     * Register a new user
     * </p>
     * <p>
     * 创建新用户账户。邮箱地址必须唯一。
     * 密码将使用 BCrypt 算法加密后存储。
     * </p>
     * <p>
     * Creates a new user account. Email address must be unique.
     * Password will be encrypted using BCrypt algorithm before storage.
     * </p>
     *
     * @param request 注册请求 / Registration request containing email, username, and password
     * @return 认证响应，包含 JWT 令牌 / Authentication response containing JWT token
     */
    AuthResponse register(RegisterRequest request);

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
     * @return 认证响应，包含 JWT 令牌 / Authentication response containing JWT token
     */
    AuthResponse login(LoginRequest request);
}
