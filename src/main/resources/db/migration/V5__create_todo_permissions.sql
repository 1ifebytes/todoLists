-- V5: RBAC sharing table — grants EDITOR or VIEWER access to a todo.
-- role: 'EDITOR' (can update) | 'VIEWER' (read-only). OWNER is not stored here;
--   ownership is determined by todos.owner_id.
-- granted_by: the user who issued the share (nullable — SET NULL if granter is deleted).
-- uq_tp_todo_user: one permission row per (todo, user) pair; re-sharing updates the existing row.
-- ON DELETE CASCADE on todo_id/user_id: permissions are cleaned up when todo or user is removed.
CREATE TABLE todo_permissions (
    id         VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '权限授权唯一标识，UUID格式 (Permission grant unique identifier, UUID format)',
    todo_id    VARCHAR(36) NOT NULL COMMENT '待办事项ID，外键关联todos表 (Todo ID, foreign key to todos table)',
    user_id    VARCHAR(36) NOT NULL COMMENT '被授权用户ID，外键关联users表 (Granted user ID, foreign key to users table)',
    role       VARCHAR(20) NOT NULL COMMENT '角色：EDITOR(可编辑) | VIEWER(只读) (Role: EDITOR(can update) | VIEWER(read-only))',
    granted_by VARCHAR(36) COMMENT '授权者用户ID，外键关联users表 (Granter user ID, foreign key to users table)',
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '授权时间 (Grant timestamp)',
    CONSTRAINT fk_tp_todo       FOREIGN KEY (todo_id)    REFERENCES todos(id)  ON DELETE CASCADE,
    CONSTRAINT fk_tp_user       FOREIGN KEY (user_id)    REFERENCES users(id)  ON DELETE CASCADE,
    CONSTRAINT fk_tp_granted_by FOREIGN KEY (granted_by) REFERENCES users(id)  ON DELETE SET NULL,
    CONSTRAINT uq_tp_todo_user  UNIQUE (todo_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT '待办事项权限授权表，实现RBAC共享 (RBAC sharing table granting EDITOR/VIEWER access to todos)';
