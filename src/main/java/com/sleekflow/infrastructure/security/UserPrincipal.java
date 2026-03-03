package com.sleekflow.infrastructure.security;

import com.sleekflow.domain.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 用户主体类
 * <p>
 * User Principal Class
 * </p>
 * <p>
 * Spring Security 主体类，包装领域 User 实体。
 * </p>
 * <p>
 * Spring Security principal wrapping the domain User entity.
 * </p>
 * <p>
 * 暴露 getId() 方法，使控制器和服务可以获取认证用户的 UUID。
 * </p>
 * <p>
 * Exposes getId() so controllers and services can obtain the authenticated user's UUID.
 * </p>
 * <p>
 * <b>角色权限说明（Role & Authority Note）：</b></p>
 * <p>
 * 此应用没有 Spring Security 角色层次结构 — RBAC 在服务层强制执行。
 * </p>
 * <p>
 * This app has no Spring Security role hierarchy — RBAC is enforced in the service layer.
 * </p>
 * <p>
 * getAuthorities() 返回空集合，所有授权检查在业务逻辑中完成。
 * </p>
 * <p>
 * getAuthorities() returns an empty collection; all authorization checks are done in business logic.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Getter
public class UserPrincipal implements UserDetails {

    /**
     * 用户 ID（UUID）
     * <p>
     * User unique identifier (UUID)
     * </p>
     */
    private final String id;

    /**
     * 用户邮箱
     * <p>
     * User email address
     * </p>
     */
    private final String email;

    /**
     * 显示用户名
     * <p>
     * Display username
     * </p>
     */
    private final String displayUsername;

    /**
     * 密码哈希值
     * <p>
     * Password hash
     * </p>
     */
    private final String password;

    /**
     * 私有构造函数
     * <p>
     * Private constructor
     * </p>
     *
     * @param id 用户 ID / User ID
     * @param email 用户邮箱 / User email
     * @param displayUsername 显示用户名 / Display username
     * @param password 密码哈希值 / Password hash
     */
    private UserPrincipal(String id, String email, String displayUsername, String password) {
        this.id = id;
        this.email = email;
        this.displayUsername = displayUsername;
        this.password = password;
    }

    /**
     * 从领域 User 实体创建 UserPrincipal
     * <p>
     * Create UserPrincipal from domain User entity
     * </p>
     *
     * @param user 领域用户实体 / Domain user entity
     * @return UserPrincipal 实例 / UserPrincipal instance
     */
    public static UserPrincipal create(User user) {
        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getPasswordHash()
        );
    }

    /**
     * 获取用户权限（授予的权限）
     * <p>
     * Get user authorities (granted authorities)
     * </p>
     * <p>
     * 此应用没有 Spring Security 角色层次结构 — RBAC 在服务层强制执行。
     * </p>
     * <p>
     * This app has no Spring Security role hierarchy — RBAC is enforced in the service layer.
     * </p>
     * <p>
     * 返回空集合，所有授权检查在业务逻辑中完成。
     * </p>
     * <p>
     * Returns an empty collection; all authorization checks are done in business logic.
     * </p>
     *
     * @return 空集合 / Empty collection
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    /**
     * 获取用户密码
     * <p>
     * Get user password
     * </p>
     *
     * @return 密码哈希值 / Password hash
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * 获取用户名
     * <p>
     * Get username
     * </p>
     * <p>
     * Spring Security 的 "username" 概念在此应用中映射到邮箱。
     * </p>
     * <p>
     * Spring Security "username" concept maps to email in this app.
     * </p>
     *
     * @return 用户邮箱 / User email
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * 检查账户是否未过期
     * <p>
     * Check if account is non-expired
     * </p>
     * <p>
     * 此应用不实现账户过期功能，始终返回 true。
     * </p>
     * <p>
     * This app does not implement account expiration, always returns true.
     * </p>
     *
     * @return true / true
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 检查账户是否未锁定
     * <p>
     * Check if account is non-locked
     * </p>
     * <p>
     * 此应用不实现账户锁定功能，始终返回 true。
     * </p>
     * <p>
     * This app does not implement account locking, always returns true.
     * </p>
     *
     * @return true / true
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 检查凭证是否未过期
     * <p>
     * Check if credentials are non-expired
     * </p>
     * <p>
     * 此应用不实现凭证过期功能，始终返回 true。
     * </p>
     * <p>
     * This app does not implement credential expiration, always returns true.
     * </p>
     *
     * @return true / true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 检查账户是否启用
     * <p>
     * Check if account is enabled
     * </p>
     * <p>
     * 此应用不实现账户禁用功能，始终返回 true。
     * </p>
     * <p>
     * This app does not implement account disabling, always returns true.
     * </p>
     *
     * @return true / true
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
