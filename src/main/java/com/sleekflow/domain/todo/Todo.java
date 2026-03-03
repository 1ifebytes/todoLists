package com.sleekflow.domain.todo;

import com.sleekflow.domain.todo.enums.Priority;
import com.sleekflow.domain.todo.enums.TodoStatus;
import com.sleekflow.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 待办事项实体
 * <p>
 * Todo Item Entity
 * </p>
 * <p>
 * 表示系统中的待办事项，支持软删除、标签分类、权限分享等功能。
 * </p>
 * <p>
 * Represents a todo item in the system, supporting soft delete, tagging, and permission-based sharing.
 * </p>
 * <p>
 * <b>软删除机制（Soft Delete）：</b>
 * </p>
 * <p>
 * <b>Soft Delete Mechanism:</b>
 * </p>
 * <ul>
 *   <li>所有 JPA 查询自动排除已软删除的行（deleted_at IS NOT NULL）/ All JPA queries automatically exclude soft-deleted rows</li>
 *   <li>使用 softDelete() 方法而不是 repository.delete() / Use softDelete() instead of repository.delete()</li>
 *   <li>数据永远不会从数据库中物理删除 / Rows are never physically removed from the database</li>
 * </ul>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Entity
@Table(name = "todos")
@SQLRestriction("deleted_at IS NULL")  // 自动排除已软删除的行 / Automatically exclude soft-deleted rows
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Todo {

    /**
     * 待办事项唯一标识符
     * <p>
     * Todo item unique identifier
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 待办事项名称
     * <p>
     * Todo item name/title
     * </p>
     * <p>
     * 注意：字段名是 'name'，不是 'title' — 与 API 规范和 DDL 保持一致。
     * </p>
     * <p>
     * Note: field is 'name', not 'title' — matches API spec and DDL.
     * </p>
     */
    @Column(nullable = false)
    private String name;

    /**
     * 待办事项详细描述
     * <p>
     * Todo item detailed description
     * </p>
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 待办事项截止日期
     * <p>
     * Todo item due date
     * </p>
     */
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    /**
     * 待办事项状态
     * <p>
     * Todo item status
     * </p>
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TodoStatus status;

    /**
     * 待办事项优先级
     * <p>
     * Todo item priority level
     * </p>
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority;

    /**
     * 待办事项所有者
     * <p>
     * Todo item owner (creator)
     * </p>
     * <p>
     * 所有者拥有全部权限（创建、读取、更新、删除、分享权限）。
     * </p>
     * <p>
     * Owner has full permissions (create, read, update, delete, share permissions).
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /**
     * 待办事项关联的标签集合
     * <p>
     * Set of tags associated with this todo item
     * </p>
     * <p>
     * ManyToMany 关系的拥有方，控制 todo_tags 连接表。
     * </p>
     * <p>
     * Owning side of the ManyToMany relationship — controls the todo_tags join table.
     * </p>
     * <p>
     * 注意：使用构建器时，Service 层总是显式传递 Set。
     * </p>
     * <p>
     * Note: Service layer always passes an explicit Set when using the builder.
     * </p>
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "todo_tags",
            joinColumns = @JoinColumn(name = "todo_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    /**
     * 待办事项的权限授权列表
     * <p>
     * List of permission grants for this todo item
     * </p>
     * <p>
     * 便利集合，用于仅限所有者的权限列表端点。
     * </p>
     * <p>
     * Convenience collection — used in OWNER-only permission list endpoint.
     * </p>
     */
    @OneToMany(mappedBy = "todo", fetch = FetchType.LAZY)
    private List<TodoPermission> permissions = new ArrayList<>();

    /**
     * 待办事项创建时间
     * <p>
     * Todo item creation timestamp
     * </p>
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 待办事项最后更新时间
     * <p>
     * Todo item last update timestamp
     * </p>
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 待办事项软删除时间
     * <p>
     * Todo item soft delete timestamp
     * </p>
     * <p>
     * NULL = 活跃状态；非 NULL = 已软删除。
     * </p>
     * <p>
     * NULL = active; non-NULL = soft-deleted.
     * </p>
     * <p>
     * 注意：永远不要直接设置此字段 — 请调用 softDelete() 方法。
     * </p>
     * <p>
     * Note: Never set this field directly — call softDelete() method instead.
     * </p>
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 软删除待办事项
     * <p>
     * Soft delete the todo item
     * </p>
     * <p>
     * 将 deletedAt 字段设置为当前时间，标记为已删除。
     * </p>
     * <p>
     * Sets deletedAt to current time, marking the todo as deleted.
     * </p>
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
