package com.sleekflow.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性
 * <p>
 * JWT Configuration Properties
 * </p>
 * <p>
 * 从 application.properties 中加载 JWT 相关的配置属性。
 * </p>
 * <p>
 * Loads JWT-related configuration properties from application.properties.
 * </p>
 * <p>
 * <b>配置前缀（Configuration Prefix）：</b> app.jwt
 * </p>
 * <ul>
 *   <li>app.jwt.secret — JWT 签名密钥（至少 32 字符，256 位）/ JWT signing key (min 32 chars, 256 bits)</li>
 *   <li>app.jwt.expiration-ms — 令牌有效期（毫秒）/ Token lifetime in milliseconds (default: 24 hours)</li>
 * </ul>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Component
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {

    /**
     * JWT 签名密钥
     * <p>
     * JWT signing secret key
     * </p>
     * <p>
     * 用于 HMAC-SHA256 签名，最少 32 个字符（256 位）。
     * </p>
     * <p>
     * Used for HMAC-SHA256 signing, minimum 32 characters (256 bits).
     * </p>
     * <p>
     * 在 application.properties 中配置。
     * </p>
     * <p>
     * Configured in application.properties.
     * </p>
     */
    private String secret;

    /**
     * JWT 令牌有效期（毫秒）
     * <p>
     * JWT token expiration time in milliseconds
     * </p>
     * <p>
     * 令牌从签发开始的有效期时长。
     * </p>
     * <p>
     * Token lifetime from issuance.
     * </p>
     * <p>
     * 默认值：86400000 = 24 小时。
     * </p>
     * <p>
     * Default: 86400000 = 24 hours.
     * </p>
     */
    private long expirationMs;
}
