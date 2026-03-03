package com.sleekflow.interfaces.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 认证响应类
 * <p>
 * Authentication Response Class
 * </p>
 * <p>
 * 用户注册或登录成功后返回的认证信息。
 * </p>
 * <p>
 * Authentication information returned after successful user registration or login.
 * </p>
 * <p>
 * <b>响应格式（Response Format）：</b></p>
 * <pre>
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "tokenType": "Bearer",
 *   "expiresIn": 86400,
 *   "user": {
 *     "id": "uuid",
 *     "email": "user@example.com",
 *     "username": "username"
 *   }
 * }
 * </pre>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /**
     * JWT 令牌
     * <p>
     * JWT token
     * </p>
     * <p>
     * 用于后续 API 请求的身份验证。
     * </p>
     * <p>
     * Used for authentication in subsequent API requests.
     * </p>
     * <p>
     * 在请求头中添加：Authorization: Bearer &lt;token&gt;
     * </p>
     * <p>
     * Add to request header: Authorization: Bearer &lt;token&gt;
     * </p>
     */
    private String token;

    /**
     * 令牌类型
     * <p>
     * Token type
     * </p>
     * <p>
     * 固定为 "Bearer"。
     * </p>
     * <p>
     * Always "Bearer".
     * </p>
     */
    private String tokenType;

    /**
     * 令牌有效期（秒）
     * <p>
     * Token expiration time in seconds
     * </p>
     * <p>
     * 令牌从签发开始的有效期时长。
     * 默认值：86400 秒 = 24 小时。
     * </p>
     * <p>
     * Token lifetime from issuance.
     * Default: 86400 seconds = 24 hours.
     * </p>
     * <p>
     * 对应配置：app.jwt.expiration-ms / 1000
     * </p>
     * <p>
     * Corresponds to: app.jwt.expiration-ms / 1000
     * </p>
     */
    private long expiresIn;

    /**
     * 用户信息摘要
     * <p>
     * User information summary
     * </p>
     * <p>
     * 包含用户 ID、邮箱和用户名。
     * </p>
     * <p>
     * Contains user ID, email, and username.
     * </p>
     */
    private UserSummaryResponse user;
}
