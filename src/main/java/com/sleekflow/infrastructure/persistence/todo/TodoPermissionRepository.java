package com.sleekflow.infrastructure.persistence.todo;

import com.sleekflow.domain.todo.TodoPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 待办事项权限仓储接口
 * <p>
 * Todo Permission Repository Interface
 * </p>
 * <p>
 * 提供 TodoPermission 实体的数据库访问操作。
 * </p>
 * <p>
 * Provides database access operations for the TodoPermission entity.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Repository
public interface TodoPermissionRepository extends JpaRepository<TodoPermission, String> {

    /**
     * 根据待办事项 ID 和用户 ID 查找权限
     * <p>
     * Find permission by todo ID and user ID
     * </p>
     * <p>
     * 由 TodoServiceImpl 中的 resolveRole() 使用，用于确定调用者的访问级别。
     * </p>
     * <p>
     * Used by resolveRole() in TodoServiceImpl to determine caller's access level.
     * </p>
     *
     * @param todoId 待办事项 ID / Todo ID
     * @param userId 用户 ID / User ID
     * @return 权限对象的 Optional 包装 / Optional wrapping the permission object
     */
    Optional<TodoPermission> findByTodoIdAndUserId(String todoId, String userId);

    /**
     * 查找待办事项的所有权限授权
     * <p>
     * Find all permission grants for a todo
     * </p>
     * <p>
     * 用于仅限所有者的权限列表端点。
     * </p>
     * <p>
     * Used in OWNER-only permission list endpoint.
     * </p>
     *
     * @param todoId 待办事项 ID / Todo ID
     * @return 权限授权列表 / List of permission grants
     */
    List<TodoPermission> findAllByTodoId(String todoId);

    /**
     * 删除用户对待办事项的访问权限
     * <p>
     * Delete user's access to a todo
     * </p>
     * <p>
     * 用于撤销用户访问权限时。
     * </p>
     * <p>
     * Used when revoking a user's access.
     * </p>
     *
     * @param todoId 待办事项 ID / Todo ID
     * @param userId 用户 ID / User ID
     */
    void deleteByTodoIdAndUserId(String todoId, String userId);

    /**
     * 检查用户是否已有待办事项的权限
     * <p>
     * Check if user already has permission for a todo
     * </p>
     *
     * @param todoId 待办事项 ID / Todo ID
     * @param userId 用户 ID / User ID
     * @return true 如果权限已存在，否则 false / true if permission exists, false otherwise
     */
    boolean existsByTodoIdAndUserId(String todoId, String userId);
}
