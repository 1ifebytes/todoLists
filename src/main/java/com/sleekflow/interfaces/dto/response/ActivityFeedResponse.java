package com.sleekflow.interfaces.dto.response;

import com.sleekflow.domain.todo.enums.TodoAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 活动记录响应类
 * <p>
 * Activity Feed Response Class
 * </p>
 * <p>
 * 待办事项活动记录的详细信息，用于审计和历史跟踪。
 * </p>
 * <p>
 * Detailed information of a todo activity feed entry, used for audit and history tracking.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityFeedResponse {

    /**
     * 活动记录 ID
     * <p>
     * Activity feed entry ID
     * </p>
     */
    private String id;

    /**
     * 待办事项 ID
     * <p>
     * Todo item ID
     * </p>
     * <p>
     * 从 ActivityFeed.todo.id 扁平化得到。
     * </p>
     * <p>
     * Flattened from ActivityFeed.todo.id.
     * </p>
     */
    private String todoId;

    /**
     * 操作者（执行操作的用户）
     * <p>
     * Actor (user who performed the action)
     * </p>
     */
    private UserSummaryResponse actor;

    /**
     * 操作类型
     * <p>
     * Action type
     * </p>
     * <p>
     * 可能的值：TODO_CREATED, TODO_UPDATED, TODO_DELETED, TODO_SHARED, TODO_UNSHARED
     * </p>
     * <p>
     * Possible values: TODO_CREATED, TODO_UPDATED, TODO_DELETED, TODO_SHARED, TODO_UNSHARED
     * </p>
     */
    private TodoAction action;

    /**
     * 操作负载（变更字段快照）
     * <p>
     * Action payload (snapshot of changed fields)
     * </p>
     * <p>
     * JSON 字符串格式，存储变更字段的快照。
     * </p>
     * <p>
     * JSON string format storing snapshot of changed fields.
     * </p>
     * <p>
     * 例如：{"name":"Buy milk", "status":"COMPLETED"}
     * </p>
     */
    private String payload;

    /**
     * 活动记录创建时间
     * <p>
     * Activity feed entry creation timestamp
     * </p>
     */
    private LocalDateTime createdAt;
}
