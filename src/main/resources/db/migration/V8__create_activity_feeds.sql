-- V8: Immutable audit log for todo mutations.
-- action: TODO_CREATED | TODO_UPDATED | TODO_DELETED | TODO_SHARED | TODO_UNSHARED.
-- payload: JSON snapshot of changed fields after the action (e.g. {"name":"Buy milk"}).
--   Stored as MySQL JSON type for structured storage; read as String in Java.
-- Rows are insert-only — never updated or deleted (except via CASCADE when todo is hard-deleted,
--   which never occurs in this app since todos use soft delete).
-- idx_af_todo_id: supports fast lookup of all activities for a given todo.
-- idx_af_created_at: supports reverse-chronological ordering (ORDER BY created_at DESC).
CREATE TABLE activity_feeds (
    id         VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '活动记录唯一标识，UUID格式 (Activity record unique identifier, UUID format)',
    todo_id    VARCHAR(36) NOT NULL COMMENT '待办事项ID，外键关联todos表 (Todo ID, foreign key to todos table)',
    actor_id   VARCHAR(36) NOT NULL COMMENT '执行操作的用户ID，外键关联users表 (Actor user ID who performed the action, foreign key to users table)',
    action     VARCHAR(30) NOT NULL COMMENT '操作类型：TODO_CREATED | TODO_UPDATED | TODO_DELETED | TODO_SHARED | TODO_UNSHARED (Action type)',
    payload    JSON COMMENT '操作载荷，JSON格式存储变更后的字段快照 (Action payload, JSON snapshot of changed fields)',
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间戳 (Action timestamp)',
    CONSTRAINT fk_af_todo  FOREIGN KEY (todo_id)  REFERENCES todos(id)  ON DELETE CASCADE,
    CONSTRAINT fk_af_actor FOREIGN KEY (actor_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_af_todo_id    (todo_id),
    INDEX idx_af_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT '待办事项活动记录表，不可变审计日志 (Immutable audit log for todo mutations)';
