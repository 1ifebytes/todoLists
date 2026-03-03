package com.sleekflow.interfaces.rest;

import com.sleekflow.application.auth.IAuthService;
import com.sleekflow.interfaces.dto.request.LoginRequest;
import com.sleekflow.interfaces.dto.request.RegisterRequest;
import com.sleekflow.interfaces.dto.response.ApiResponse;
import com.sleekflow.interfaces.dto.response.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * <p>
 * Authentication Controller
 * </p>
 * <p>
 * 提供用户认证相关的 REST API 端点，包括注册和登录。
 * </p>
 * <p>
 * Provides REST API endpoints for user authentication, including registration and login.
 * </p>
 * <p>
 * <b>端点（Endpoints）：</b></p>
 * <ul>
 *   <li>POST /api/v1/auth/register - 用户注册 / User registration</li>
 *   <li>POST /api/v1/auth/login - 用户登录 / User login</li>
 * </ul>
 * <p>
 * <b>注意（Note）：</b>这些端点不需要认证，公开访问。
 * </p>
 * <p>
 * These endpoints do not require authentication and are publicly accessible.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication endpoints")
public class AuthController {

    private final IAuthService authService;

    /**
     * 用户注册
     * <p>
     * Register a new user
     * </p>
     * <p>
     * 创建新用户账户。邮箱地址必须唯一。
     * 密码将使用 BCrypt 算法加密后存储。
     * 注册成功后返回 JWT 令牌，用于后续 API 调用的身份验证。
     * </p>
     * <p>
     * Creates a new user account. Email address must be unique.
     * Password will be encrypted using BCrypt algorithm.
     * Returns a JWT token upon successful registration for authentication in subsequent API calls.
     * </p>
     *
     * @param request 注册请求体 / Request body containing email, username, and password
     * @return 包含 JWT 令牌和用户信息的响应，HTTP 201 状态码 / Response containing JWT token and user info with HTTP 201 status
     */
    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(authService.register(request)));
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
     * <p>
     * <b>令牌使用（Token Usage）：</b></p>
     * <pre>
     * 在后续请求的 Header 中添加：
     * Authorization: Bearer &lt;token&gt;
     *
     * Add to request header:
     * Authorization: Bearer &lt;token&gt;
     * </pre>
     *
     * @param request 登录请求体 / Request body containing email and password
     * @return 包含 JWT 令牌和用户信息的响应 / Response containing JWT token and user info
     */
    @Operation(summary = "Login with email and password")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(request)));
    }
}
