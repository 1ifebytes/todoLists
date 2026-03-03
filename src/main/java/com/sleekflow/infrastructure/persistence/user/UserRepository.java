package com.sleekflow.infrastructure.persistence.user;

import com.sleekflow.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户仓储接口
 * <p>
 * User Repository Interface
 * </p>
 * <p>
 * 提供 User 实体的数据库访问操作。
 * </p>
 * <p>
 * Provides database access operations for the User entity.
 * </p>
 * <p>
 * 继承 JpaRepository，提供基础的 CRUD 操作。
 * </p>
 * <p>
 * Extends JpaRepository, providing basic CRUD operations.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * 根据邮箱查找用户
     * <p>
     * Find user by email
     * </p>
     * <p>
     * 用于登录时查找用户。
     * </p>
     * <p>
     * Used for finding user during login.
     * </p>
     *
     * @param email 用户邮箱 / User email
     * @return 用户对象的 Optional 包装 / Optional wrapping the user object
     */
    Optional<User> findByEmail(String email);

    /**
     * 检查邮箱是否已存在
     * <p>
     * Check if email already exists
     * </p>
     * <p>
     * 用于注册时检查邮箱唯一性。
     * </p>
     * <p>
     * Used for checking email uniqueness during registration.
     * </p>
     *
     * @param email 用户邮箱 / User email
     * @return true 如果邮箱已存在，否则 false / true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
