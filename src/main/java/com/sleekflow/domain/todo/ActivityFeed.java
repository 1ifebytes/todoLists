package com.sleekflow.domain.todo;

import com.sleekflow.domain.todo.enums.TodoAction;
import com.sleekflow.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 活动记录实体
 * <p>
 * Activity Feed Entity
 * </p>
 * <p>
 * 表示待办事项上发生的操作审计日志，用于跟踪所有变更历史。
 * </p>
 * <p>
 * Represents audit log entries for actions performed on todo items, used to track all change history.
 * </p>
 * <p>
 * <b>不可变日志（Immutable Audit Log）：</b></p>
 * <ul>
 *   <li>行只插入，在应用代码中永不更新或删除 / Rows are insert-only, never updated or deleted in application code</li>
 *   <li>payload 存储变更字段的 JSON 快照 / payload stores a JSON string snapshot of the changed fields</li>
 *   <li>例如：{"name":"Buy milk"} 或 {"status":"COMPLETED"} / Example: {"name":"Buy milk"} or {"status":"COMPLETED"}</li>
 * </ul>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Entity
@Table(name = "activity_feeds")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityFeed {

    /**
     * 活动记录唯一标识符
     * <p>
     * Activity feed entry unique identifier
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 关联的待办事项
     * <p>
     * Associated todo item
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", nullable = false)
    private Todo todo;

    /**
     * 执行操作的用户（操作者）
     * <p>
     * User who performed the action (actor)
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    /**
     * 操作类型
     * <p>
     * Type of action performed
     * </p>
     * <p>
     * 可能的值：TODO_CREATED, TODO_UPDATED, TODO_DELETED, TODO_SHARED, TODO_UNSHARED
     * </p>
     * <p>
     * Possible values: TODO_CREATED, TODO_UPDATED, TODO_DELETED, TODO_SHARED, TODO_UNSHARED
     * </p>
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
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
     * JSON string format — snapshot of changed fields.
     * </p>
     * <p>
     * 存储为 TEXT 类型，映射到 MySQL JSON 列。
     * </p>
     * <p>
     * Stored as TEXT type, mapped to MySQL JSON column.
     * </p>
     * <p>
     * 例如：{"name":"Buy milk", "status":"COMPLETED"}
     * </p>
     * <p>
     * Example: {"name":"Buy milk", "status":"COMPLETED"}
     * </p>
     */
    @Column(columnDefinition = "JSON")
    private String payload;

    /**
     * 活动记录创建时间
     * <p>
     * Activity feed entry creation timestamp
     * </p>
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
