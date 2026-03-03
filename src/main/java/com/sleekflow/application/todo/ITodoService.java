package com.sleekflow.application.todo;

import com.sleekflow.domain.todo.enums.Priority;
import com.sleekflow.domain.todo.enums.TodoStatus;
import com.sleekflow.interfaces.dto.request.CreateTodoRequest;
import com.sleekflow.interfaces.dto.request.ShareTodoRequest;
import com.sleekflow.interfaces.dto.request.UpdateTodoRequest;
import com.sleekflow.interfaces.dto.response.PageResponse;
import com.sleekflow.interfaces.dto.response.TodoPermissionResponse;
import com.sleekflow.interfaces.dto.response.TodoResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 待办事项服务接口
 * <p>
 * Todo Service Interface
 * </p>
 * <p>
 * 定义待办事项（Todo）相关的业务操作，包括创建、查询、更新、删除、权限管理和活动记录等功能。
 * </p>
 * <p>
 * Defines business operations for todo items, including creation, querying, updating, deletion,
 * permission management, and activity tracking.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
public interface ITodoService {

    /**
     * 创建新的待办事项
     * <p>
     * Create a new todo item
     * </p>
     * <p>
     * 为当前认证用户创建新的待办事项，创建者自动成为待办事项的所有者（OWNER）。
     * </p>
     * <p>
     * Creates a new todo item for the current authenticated user. The creator automatically becomes the owner (OWNER).
     * </p>
     *
     * @param request 创建待办事项请求 / Request containing todo details
     * @return 创建的待办事项响应 / Created todo response
     */
    TodoResponse createTodo(CreateTodoRequest request);

    /**
     * 查询待办事项列表
     * <p>
     * List todos with optional filters and pagination
     * </p>
     * <p>
     * 查询当前用户可见的所有待办事项，支持多维度过滤和分页。
     * </p>
     * <p>
     * Retrieves all todos visible to the current user, supporting multi-dimensional filtering and pagination.
     * </p>
     *
     * @param status 状态过滤条件（可选） / Status filter (optional)
     * @param priority 优先级过滤条件（可选） / Priority filter (optional)
     * @param dueDateFrom 截止日期范围开始（可选）/ Due date range start (optional)
     * @param dueDateTo 截止日期范围结束（可选）/ Due date range end (optional)
     * @param tagNames 标签名称过滤条件（可选）/ Tag name filter (optional)
     * @param owned 所有权过滤：true=仅拥有的，false=仅分享的（可选）/ Ownership filter: true=owned only, false=shared only (optional)
     * @param pageable 分页和排序参数 / Pagination and sorting parameters
     * @return 分页待办事项列表响应 / Paginated todo list response
     */
    PageResponse<TodoResponse> listTodos(TodoStatus status,
                                         Priority priority,
                                         LocalDateTime dueDateFrom,
                                         LocalDateTime dueDateTo,
                                         List<String> tagNames,
                                         Boolean owned,
                                         Pageable pageable);

    /**
     * 根据 ID 查询待办事项详情
     * <p>
     * Get todo details by ID
     * </p>
     * <p>
     * 查询指定待办事项的详细信息。用户只能查询自己拥有的或已被分享的待办事项。
     * </p>
     * <p>
     * Retrieves detailed information of a specific todo by ID.
     * Users can only query todos they own or have been shared with them.
     * </p>
     *
     * @param todoId 待办事项 ID / Todo item ID
     * @return 待办事项响应 / Todo response
     */
    TodoResponse getTodo(String todoId);

    /**
     * 更新待办事项
     * <p>
     * Update a todo item
     * </p>
     * <p>
     * 更新指定待办事项的信息。仅应用请求中的非空字段（部分更新，PUT 语义）。
     * 只有所有者（OWNER）和编辑者（EDITOR）可以更新待办事项。
     * </p>
     * <p>
     * Updates the specified todo item. Only non-null fields in the request are applied (partial update, PUT semantics).
     * Only todo owners (OWNER) and editors (EDITOR) can update todos.
     * </p>
     *
     * @param todoId 待办事项 ID / Todo item ID
     * @param request 更新待办事项请求 / Request containing fields to update
     * @return 更新后的待办事项响应 / Updated todo response
     */
    TodoResponse updateTodo(String todoId, UpdateTodoRequest request);

    /**
     * 删除待办事项（软删除）
     * <p>
     * Delete a todo item (soft delete)
     * </p>
     * <p>
     * 标记指定待办事项为已删除（软删除）。只有待办事项的所有者（OWNER）可以执行删除操作。
     * 同时记录 TODO_DELETED 活动日志。
     * </p>
     * <p>
     * Marks the specified todo item as deleted (soft delete). Only todo owners (OWNER) can perform deletion.
     * Also records a TODO_DELETED activity log entry.
     * </p>
     *
     * @param todoId 待办事项 ID / Todo item ID
     */
    void deleteTodo(String todoId);

    /**
     * 分享待办事项给其他用户
     * <p>
     * Share a todo with another user
     * </p>
     * <p>
     * 将指定待办事项分享给其他用户，并授予指定的角色权限（EDITOR 或 VIEWER）。
     * 只有待办事项的所有者（OWNER）可以执行分享操作。
     * </p>
     * <p>
     * Shares the specified todo item with another user and grants the specified role (EDITOR or VIEWER).
     * Only todo owners (OWNER) can perform the sharing operation.
     * </p>
     *
     * @param todoId 待办事项 ID / Todo item ID
     * @param request 分享待办事项请求 / Request with target user ID and role
     * @return 创建的权限响应 / Created permission response
     */
    TodoPermissionResponse shareTodo(String todoId, ShareTodoRequest request);

    /**
     * 撤销用户对待办事项的访问权限
     * <p>
     * Revoke a user's access to a todo
     * </p>
     * <p>
     * 移除指定用户对待办事项的访问权限。
     * 只有待办事项的所有者（OWNER）可以执行此操作。
     * </p>
     * <p>
     * Revokes the specified user's access to the todo item.
     * Only todo owners (OWNER) can perform this operation.
     * </p>
     *
     * @param todoId 待办事项 ID / Todo item ID
     * @param targetUserId 被撤销权限的用户 ID / ID of the user whose access is being revoked
     */
    void unshareTodo(String todoId, String targetUserId);

    /**
     * 查询待办事项的所有权限授权列表
     * <p>
     * List all permission grants for a todo
     * </p>
    * <p>
     * 查询指定待办事项的所有权限授权记录（EDITOR 和 VIEWER），包括被分享用户及其角色权限。
     * 只有待办事项的所有者（OWNER）可以查询。
     * </p>
     * <p>
     * Retrieves all permission grants (EDITOR and VIEWER) for the specified todo,
     * including shared users and their roles. Only todo owners (OWNER) can query permissions.
     * </p>
     *
     * @param todoId 待办事项 ID / Todo item ID
     * @return 权限授权列表响应 / List of permission grants response
     */
    List<TodoPermissionResponse> listPermissions(String todoId);
}
