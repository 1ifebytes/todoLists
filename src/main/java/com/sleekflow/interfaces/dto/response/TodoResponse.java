package com.sleekflow.interfaces.dto.response;

import com.sleekflow.domain.todo.enums.Priority;
import com.sleekflow.domain.todo.enums.TodoRole;
import com.sleekflow.domain.todo.enums.TodoStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 待办事项响应类
 * <p>
 * Todo Item Response Class
 * </p>
 * <p>
 * 待办事项的详细信息响应。
 * </p>
 * <p>
 * Detailed information response for a todo item.
 * </p>
 * <p>
 * <b>响应格式（Response Format）：</b></p>
 * <pre>
 * {
 *   "id": "uuid",
 *   "name": "Review PR #42",
 *   "description": "Review the pull request for bug fix",
 *   "dueDate": "2025-06-15T10:00:00",
 *   "status": "IN_PROGRESS",
 *   "priority": "HIGH",
 *   "owner": { "id": "uuid", "email": "...", "username": "..." },
 *   "tags": [ { "id": "uuid", "name": "work" } ],
 *   "myRole": "EDITOR",
 *   "createdAt": "2025-01-15T10:00:00",
 *   "updatedAt": "2025-01-15T11:30:00"
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
public class TodoResponse {

    /**
     * 待办事项 ID
     * <p>
     * Todo item ID
     * </p>
     */
    private String id;

    /**
     * 待办事项名称
     * <p>
     * Todo item name
     * </p>
     * <p>
     * 注意：字段是 'name'，不是 'title'。
     * </p>
     * <p>
     * Note: field is 'name', not 'title'.
     * </p>
     */
    private String name;

    /**
     * 待办事项描述
     * <p>
     * Todo item description
     * </p>
     */
    private String description;

    /**
     * 截止日期
     * <p>
     * Due date
     * </p>
     */
    private LocalDateTime dueDate;

    /**
     * 待办事项状态
     * <p>
     * Todo item status
     * </p>
     * <p>
     * 可能的值：NOT_STARTED, IN_PROGRESS, COMPLETED
     * </p>
     * <p>
     * Possible values: NOT_STARTED, IN_PROGRESS, COMPLETED
     * </p>
     */
    private TodoStatus status;

    /**
     * 待办事项优先级
     * <p>
     * Todo item priority
     * </p>
     * <p>
     * 可能的值：LOW, MEDIUM, HIGH
     * </p>
     * <p>
     * Possible values: LOW, MEDIUM, HIGH
     * </p>
     */
    private Priority priority;

    /**
     * 待办事项所有者
     * <p>
     * Todo item owner
     * </p>
     */
    private UserSummaryResponse owner;

    /**
     * 关联的标签列表
     * <p>
     * List of associated tags
     * </p>
     */
    private List<TagResponse> tags;

    /**
     * 调用者的有效角色
     * <p>
     * Caller's effective role
     * </p>
     * <p>
     * 调用者对此待办事项的访问权限级别。
     * 由服务层在映射后设置。
     * </p>
     * <p>
     * Caller's access permission level for this todo.
     * Set by service layer after mapping.
     * </p>
     * <p>
     * 可能的值：OWNER, EDITOR, VIEWER
     * </p>
     * <p>
     * Possible values: OWNER, EDITOR, VIEWER
     * </p>
     */
    private TodoRole myRole;

    /**
     * 创建时间
     * <p>
     * Creation timestamp
     * </p>
     */
    private LocalDateTime createdAt;

    /**
     * 最后更新时间
     * <p>
     * Last update timestamp
     * </p>
     */
    private LocalDateTime updatedAt;
}
