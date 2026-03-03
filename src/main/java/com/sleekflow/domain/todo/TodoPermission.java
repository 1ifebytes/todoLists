package com.sleekflow.domain.todo;

import com.sleekflow.domain.todo.enums.TodoRole;
import com.sleekflow.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 待办事项权限实体
 * <p>
 * Todo Permission Entity
 * </p>
 * <p>
 * 表示用户对待办事项的访问权限授权。
 * 存储 EDITOR 或 VIEWER 角色的授权。OWNER 角色从 todos.owner_id 派生，不存储在此表中。
 * </p>
 * <p>
 * Represents access permission grants for users on todo items.
 * Stores EDITOR or VIEWER role grants. The OWNER role is derived from todos.owner_id and is not stored here.
 * </p>
 * <p>
 * <b>业务规则（Business Rules）：</b></p>
 * <ul>
 *   <li>每个 (todo, user) 对只能有一行记录，由 uq_tp_todo_user 唯一约束强制执行 / One row per (todo, user) pair enforced by uq_tp_todo_user unique constraint</li>
 *   <li>OWNER 角色从不存储在此表中 / OWNER role is never stored in this table</li>
 *   <li>EDITOR 可以更新待办事项 / EDITOR can update todo items</li>
 *   <li>VIEWER 只能查看 / VIEWER is read-only</li>
 * </ul>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Entity
@Table(name = "todo_permissions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoPermission {

    /**
     * 权限记录唯一标识符
     * <p>
     * Permission record unique identifier
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
     * 被授予权限的用户
     * <p>
     * User who has been granted access
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 授予的角色
     * <p>
     * Granted role
     * </p>
     * <p>
     * EDITOR 可以更新；VIEWER 只读。OWNER 永不存储在此处。
     * </p>
     * <p>
     * EDITOR can update; VIEWER is read-only. OWNER is never stored here.
     * </p>
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TodoRole role;

    /**
     * 授权用户（授予权限的用户）
     * <p>
     * Granting user (user who granted this permission)
     * </p>
     * <p>
     * 可为空：如果授权用户被删除，数据库中设置为 SET NULL。
     * </p>
     * <p>
     * Nullable: SET NULL in DB if the granting user is deleted.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by")
    private User grantedBy;

    /**
     * 权限授予时间
     * <p>
     * Permission grant timestamp
     * </p>
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
