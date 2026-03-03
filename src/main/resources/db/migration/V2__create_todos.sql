-- V2: Core todo items table.
-- name is used throughout (not 'title') to match the API spec.
-- status: NOT_STARTED | IN_PROGRESS | COMPLETED (stored as VARCHAR, mapped via @Enumerated(STRING)).
-- priority: LOW | MEDIUM | HIGH.
-- deleted_at: NULL = active item; non-NULL = soft-deleted.
--   The @SQLRestriction("deleted_at IS NULL") annotation on the Todo entity
--   ensures all JPA queries automatically exclude soft-deleted rows.
-- idx_todos_deleted_at speeds up the IS NULL filter on every query.
CREATE TABLE todos (
    id          VARCHAR(36)  NOT NULL PRIMARY KEY COMMENT '待办事项唯一标识，UUID格式 (Todo unique identifier, UUID format)',
    name        VARCHAR(255) NOT NULL COMMENT '待办事项标题/名称 (Todo title/name)',
    description TEXT         COMMENT '详细描述，支持长文本 (Detailed description, supports long text)',
    due_date    DATETIME     COMMENT '截止日期，可选 (Due date, optional)',
    status      VARCHAR(20)  NOT NULL DEFAULT 'NOT_STARTED' COMMENT '状态：NOT_STARTED | IN_PROGRESS | COMPLETED (Status: not started | in progress | completed)',
    priority    VARCHAR(20)  NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级：LOW | MEDIUM | HIGH (Priority: low | medium | high)',
    owner_id    VARCHAR(36)  NOT NULL COMMENT '所有者用户ID，外键关联users表 (Owner user ID, foreign key to users table)',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 (Creation timestamp)',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间，自动更新 (Last update timestamp, auto-updated)',
    deleted_at  DATETIME     DEFAULT NULL COMMENT '软删除时间，NULL=未删除，非NULL=已删除 (Soft delete timestamp: null=active, non-null=deleted)',
    CONSTRAINT fk_todos_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_todos_owner_id   (owner_id),
    INDEX idx_todos_name       (name),
    INDEX idx_todos_status     (status),
    INDEX idx_todos_priority   (priority),
    INDEX idx_todos_due_date   (due_date),
    INDEX idx_todos_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT '待办事项核心表，支持软删除 (Core todo items table with soft delete support)';
