package com.sleekflow.interfaces.rest;

import com.sleekflow.application.todo.IActivityFeedService;
import com.sleekflow.application.todo.ITodoService;
import com.sleekflow.domain.todo.enums.Priority;
import com.sleekflow.domain.todo.enums.TodoStatus;
import com.sleekflow.interfaces.dto.request.CreateTodoRequest;
import com.sleekflow.interfaces.dto.request.ShareTodoRequest;
import com.sleekflow.interfaces.dto.request.UpdateTodoRequest;
import com.sleekflow.interfaces.dto.response.ActivityFeedResponse;
import com.sleekflow.interfaces.dto.response.ApiResponse;
import com.sleekflow.interfaces.dto.response.PageResponse;
import com.sleekflow.interfaces.dto.response.TodoPermissionResponse;
import com.sleekflow.interfaces.dto.response.TodoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Todo Controller - 待办事项管理接口
 * <p>
 * Todo Controller - Todo Management API
 * </p>
 * <p>
 * 提供待办事项（Todo）的 CRUD 操作、权限管理、活动记录查询等 RESTful 接口。
 * 支持创建、查询、更新、删除待办事项，以及与其他用户分享、权限管理、活动历史记录等功能。
 * </p>
 * <p>
 * Provides CRUD operations, permission management, and activity feed queries for Todo items.
 * Supports creating, querying, updating, deleting todos, sharing with other users,
 * permission management, and activity history tracking.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Todos", description = "Todo management")
public class TodoController {

    private final ITodoService todoService;
    private final IActivityFeedService activityFeedService;

    /**
     * 创建新的待办事项
     * <p>
     * Create a new todo item for the authenticated user.
     * </p>
     * <p>
     * Creates a new todo item with the provided details. The creator automatically becomes the owner.
     * </p>
     *
     * @param request 创建待办事项请求体 / Request body containing todo details
     * @return 包含创建的待办事项信息的响应，HTTP 201 状态码 / Response containing created todo with HTTP 201 status
     */
    @Operation(summary = "Create a new todo")
    @PostMapping
    public ResponseEntity<ApiResponse<TodoResponse>> createTodo(
            @Valid @RequestBody CreateTodoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(todoService.createTodo(request)));
    }

    /**
     * 查询待办事项列表（支持过滤和分页）
     * <p>
     * List todos with optional filters and pagination.
     * </p>
     * <p>
     * 查询当前用户可见的所有待办事项，支持按状态、优先级、日期范围、标签、所有权等多维度过滤，
     * 支持分页和排序。返回结果包括用户拥有的待办事项以及其他用户分享给当前用户的待办事项。
     * </p>
     * <p>
     * Retrieves all todos visible to the current user, supporting multi-dimensional filtering by status,
     * priority, date range, tags, ownership, etc. Supports pagination and sorting.
     * Results include both owned todos and todos shared with the current user.
     * </p>
     *
     * @param status 待办事项状态过滤条件（可选） / Todo status filter (optional)
     * @param priority 待办事项优先级过滤条件（可选） / Todo priority filter (optional)
     * @param dueDateFrom 截止日期范围开始（可选）/ Due date range start (optional)
     * @param dueDateTo 截止日期范围结束（可选）/ Due date range end (optional)
     * @param tags 标签过滤条件，逗号分隔（可选）/ Tag filter, comma-separated (optional)
     * @param owned 所有权过滤：true=仅拥有的，false=仅分享的（可选）/ Ownership filter: true=owned only, false=shared only (optional)
     * @param page 页码，从 0 开始 / Page number, zero-based
     * @param size 每页大小 / Page size
     * @param sortBy 排序字段（默认：createdAt）/ Sort field (default: createdAt)
     * @param sortOrder 排序方向：asc 或 desc（默认：desc）/ Sort direction: asc or desc (default: desc)
     * @return 包含分页待办事项列表的响应 / Response containing paginated todo list
     */
    @Operation(summary = "List todos visible to the authenticated user with optional filters")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TodoResponse>>> listTodos(
            @RequestParam(required = false) TodoStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDateTo,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Boolean owned,
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @Max(100) @RequestParam(defaultValue = "20") int size,
            @Pattern(regexp = "name|status|priority|createdAt|updatedAt|dueDate") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Pattern(regexp = "asc|desc", flags = Pattern.Flag.CASE_INSENSITIVE) @RequestParam(defaultValue = "desc") String sortOrder) {

        List<String> tagList = (tags != null && !tags.isBlank())
                ? Arrays.asList(tags.split(","))
                : null;
        Sort sort = "asc".equalsIgnoreCase(sortOrder)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(ApiResponse.success(
                todoService.listTodos(status, priority, dueDateFrom, dueDateTo, tagList, owned, pageable)));
    }

    /**
     * 根据 ID 查询待办事项详情
     * <p>
     * Get todo details by ID.
     * </p>
     * <p>
     * 根据 ID 查询指定待办事项的详细信息。用户只能查询自己拥有的或已被分享的待办事项。
     * </p>
     * <p>
     * Retrieves detailed information of a specific todo by ID. Users can only query todos they own
     * or have been shared with them.
     * </p>
     *
     * @param id 待办事项 ID / Todo item ID
     * @return 包含待办事项详细信息的响应 / Response containing todo details
     */
    @Operation(summary = "Get a todo by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoResponse>> getTodo(
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(todoService.getTodo(id)));
    }

    /**
     * 更新待办事项
     * <p>
     * Update a todo item.
     * </p>
     * <p>
     * 更新指定待办事项的信息。仅更新请求中提供的非空字段（部分更新）。
     * 只有所有者（OWNER）和编辑者（EDITOR）可以更新待办事项。
     * </p>
     * <p>
     * Updates the specified todo item. Only non-null fields in the request are applied (partial update).
     * Only todo owners (OWNER) and editors (EDITOR) can update todos.
     * </p>
     *
     * @param id 待办事项 ID / Todo item ID
     * @param request 更新待办事项请求体 / Request body containing fields to update
     * @return 包含更新后待办事项信息的响应 / Response containing updated todo details
     */
    @Operation(summary = "Update a todo (only non-null fields are applied)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoResponse>> updateTodo(
            @PathVariable String id,
            @Valid @RequestBody UpdateTodoRequest request) {
        return ResponseEntity.ok(ApiResponse.success(todoService.updateTodo(id, request)));
    }

    /**
     * 删除待办事项（软删除）
     * <p>
     * Delete a todo item (soft delete).
     * </p>
     * <p>
     * 标记指定待办事项为已删除（软删除）。数据不会从数据库中删除，但不会再出现在查询结果中。
     * 只有待办事项的所有者（OWNER）可以执行删除操作。
     * </p>
     * <p>
     * Marks the specified todo item as deleted (soft delete). Data is not removed from the database
     * but will not appear in query results. Only todo owners (OWNER) can perform deletion.
     * </p>
     *
     * @param id 待办事项 ID / Todo item ID
     * @return HTTP 204 无内容响应 / HTTP 204 no content response
     */
    @Operation(summary = "Delete a todo (owner only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(
            @PathVariable String id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 分享待办事项给其他用户
     * <p>
     * Share a todo with another user.
     * </p>
     * <p>
     * 将指定待办事项分享给其他用户，并授予指定的角色权限（OWNER/EDITOR/VIEWER）。
     * 只有待办事项的所有者（OWNER）可以执行分享操作。
     * </p>
     * <p>
     * Shares the specified todo item with another user and grants the specified role (OWNER/EDITOR/VIEWER).
     * Only todo owners (OWNER) can perform the sharing operation.
     * </p>
     *
     * @param id 待办事项 ID / Todo item ID
     * @param request 分享待办事项请求体，包含目标用户 ID 和角色 / Request body with target user ID and role
     * @return 包含创建的权限信息的响应，HTTP 201 状态码 / Response containing created permission with HTTP 201 status
     */
    @Operation(summary = "Share a todo with another user (owner only)")
    @PostMapping("/{id}/share")
    public ResponseEntity<ApiResponse<TodoPermissionResponse>> shareTodo(
            @PathVariable String id,
            @Valid @RequestBody ShareTodoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(todoService.shareTodo(id, request)));
    }

    /**
     * 查询待办事项的所有权限授权列表
     * <p>
     * List all permission grants for a todo.
     * </p>
     * <p>
     * 查询指定待办事项的所有权限授权记录，包括被分享用户及其角色权限。
     * 只有待办事项的所有者（OWNER）可以查询。
     * </p>
     * <p>
     * Retrieves all permission grants for the specified todo, including shared users and their roles.
     * Only todo owners (OWNER) can query permissions.
     * </p>
     *
     * @param id 待办事项 ID / Todo item ID
     * @return 包含权限授权列表的响应 / Response containing list of permission grants
     */
    @Operation(summary = "List all permission grants for a todo (owner only)")
    @GetMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<List<TodoPermissionResponse>>> listPermissions(
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(todoService.listPermissions(id)));
    }

    /**
     * 撤销用户对待办事项的访问权限
     * <p>
     * Revoke a user's access to a todo.
     * </p>
     * <p>
     * 移除指定用户对待办事项的访问权限。被撤销权限的用户将无法再查看或编辑该待办事项。
     * 只有待办事项的所有者（OWNER）可以执行此操作。
     * </p>
     * <p>
     * Revokes the specified user's access to the todo item. The user will no longer be able to
     * view or edit the todo. Only todo owners (OWNER) can perform this operation.
     * </p>
     *
     * @param id 待办事项 ID / Todo item ID
     * @param userId 被撤销权限的用户 ID / ID of the user whose access is being revoked
     * @return HTTP 204 无内容响应 / HTTP 204 no content response
     */
    @Operation(summary = "Revoke a user's access to a todo (owner only)")
    @DeleteMapping("/{id}/share/{userId}")
    public ResponseEntity<Void> unshareTodo(
            @PathVariable String id,
            @PathVariable String userId) {
        todoService.unshareTodo(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 查询待办事项的活动记录
     * <p>
     * Get activity feed for a todo.
     * </p>
     * <p>
     * 查询指定待办事项的所有活动记录，包括创建、更新、状态变更、权限变更等历史操作。
     * 返回结果按时间倒序排列，支持分页查询。
     * </p>
     * <p>
     * Retrieves all activity records for the specified todo, including creation, updates, status changes,
     * permission changes, and other historical operations. Results are sorted by time in descending order
     * and support pagination.
     * </p>
     *
     * @param id 待办事项 ID / Todo item ID
     * @param page 页码，从 0 开始（默认 0）/ Page number, zero-based (default: 0)
     * @param size 每页大小（默认 20）/ Page size (default: 20)
     * @return 包含分页活动记录列表的响应 / Response containing paginated activity feed list
     */
    @Operation(summary = "Get activity feed for a todo")
    @GetMapping("/{id}/activities")
    public ResponseEntity<ApiResponse<PageResponse<ActivityFeedResponse>>> getActivities(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(
                activityFeedService.listFeed(id, pageable)));
    }
}
