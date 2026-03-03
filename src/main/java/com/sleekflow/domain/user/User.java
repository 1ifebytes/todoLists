package com.sleekflow.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 用户实体
 * <p>
 * User Entity
 * </p>
 * <p>
 * 表示系统中的用户账户，存储用户的认证信息和个人资料。
 * </p>
 * <p>
 * Represents a user account in the system, storing user authentication and profile information.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 用户唯一标识符
     * <p>
     * User unique identifier
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 用户邮箱地址
     * <p>
     * User email address (unique, used for login)
     * </p>
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * 用户名
     * <p>
     * Username (display name)
     * </p>
     */
    @Column(nullable = false, length = 100)
    private String username;

    /**
     * 密码哈希值
     * <p>
     * Password hash (BCrypt encrypted, not plain text)
     * </p>
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * 账户创建时间
     * <p>
     * Account creation timestamp
     * </p>
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 账户最后更新时间
     * <p>
     * Account last update timestamp
     * </p>
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
