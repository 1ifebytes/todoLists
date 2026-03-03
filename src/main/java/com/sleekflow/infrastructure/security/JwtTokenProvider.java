package com.sleekflow.infrastructure.security;

import com.sleekflow.infrastructure.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 令牌提供者
 * <p>
 * JWT Token Provider
 * </p>
 * <p>
 * 负责生成、解析和验证 JWT 令牌。
 * </p>
 * <p>
 * Responsible for generating, parsing, and validating JWT tokens.
 * </p>
 * <p>
 * <b>API 版本说明（API Version Note）：</b></p>
 * <p>
 * 使用 JJWT 0.12.x API（与 0.11.x 不兼容）。
 * </p>
 * <p>
 * Uses JJWT 0.12.x API (incompatible with 0.11.x).
 * </p>
 * <p>
 * <b>关键 API（Key APIs）：</b></p>
 * <ul>
 *   <li>生成：Jwts.builder()...signWith(key)</li>
 *   <li>解析：Jwts.parser().verifyWith(key).build().parseSignedClaims(token)</li>
 * </ul>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    /**
     * 生成 JWT 令牌
     * <p>
     * Generate JWT token
     * </p>
     * <p>
     * 为指定用户生成 JWT 令牌，包含用户 ID 和邮箱声明。
     * </p>
     * <p>
     * Generates a JWT token for the specified user, including user ID and email claims.
     * </p>
     *
     * @param userId 用户 ID / User ID
     * @param email 用户邮箱 / User email
     * @return JWT 令牌字符串 / JWT token string
     */
    public String generateToken(String userId, String email) {
        return Jwts.builder()
                .subject(userId)  // JWT subject = user ID
                .claim("email", email)  // Email claim
                .issuedAt(new Date())  // Issue time
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationMs()))  // Expiration
                .signWith(secretKey())  // Sign with HMAC-SHA256
                .compact();
    }

    /**
     * 从令牌中提取用户 ID
     * <p>
     * Extract user ID from token
     * </p>
     * <p>
     * 提取 JWT subject（用户 ID），由 JwtAuthenticationFilter 用于加载 UserDetails。
     * </p>
     * <p>
     * Extracts the JWT subject (user ID) — used by JwtAuthenticationFilter to load UserDetails.
     * </p>
     *
     * @param token JWT 令牌 / JWT token
     * @return 用户 ID / User ID
     */
    public String getUserId(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 验证 JWT 令牌
     * <p>
     * Validate JWT token
     * </p>
     * <p>
     * 检查令牌签名和有效期。如果令牌无效或已过期，返回 false。
     * </p>
     * <p>
     * Verifies token signature and expiration. Returns false if token is invalid or expired.
     * </p>
     *
     * @param token JWT 令牌 / JWT token
     * @return true 如果令牌有效，否则 false / true if token is valid, false otherwise
     */
    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 解析 JWT 令牌声明
     * <p>
     * Parse JWT token claims
     * </p>
     * <p>
     * 解析并返回 JWT 令牌的声明部分。
     * </p>
     * <p>
     * Parses and returns the JWT token claims.
     * </p>
     *
     * @param token JWT 令牌 / JWT token
     * @return JWT 声明对象 / JWT claims object
     * @throws JwtException 如果令牌无效 / if token is invalid
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey())  // Verify with secret key
                .build()
                .parseSignedClaims(token)  // Parse signed claims
                .getPayload();  // Get payload
    }

    /**
     * 生成 HMAC-SHA256 密钥
     * <p>
     * Generate HMAC-SHA256 secret key
     * </p>
     * <p>
     * 从 application.properties 中的纯文本密钥转换为字节数组，用于 HMAC-SHA256 签名。
     * 密钥必须至少 32 个字符（256 位）。
     * </p>
     * <p>
     * Converts the plain-text secret from application.properties to bytes for HMAC-SHA256 signing.
     * Requires secret to be at least 32 characters (256 bits).
     * </p>
     *
     * @return 密钥对象 / Secret key object
     */
    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
