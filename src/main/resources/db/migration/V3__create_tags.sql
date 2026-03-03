-- V3: User-scoped tags for categorising todos.
-- Tags are private per user: two different users can have tags with the same name.
-- uq_tags_name_user enforces uniqueness within a single user's tag list.
-- ON DELETE CASCADE: deleting a user removes all their tags.
CREATE TABLE tags (
    id         VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '标签唯一标识，UUID格式 (Tag unique identifier, UUID format)',
    name       VARCHAR(50) NOT NULL COMMENT '标签名称，如：work, urgent, shopping (Tag name, e.g.: work, urgent, shopping)',
    user_id    VARCHAR(36) NOT NULL COMMENT '所属用户ID，外键关联users表 (Owner user ID, foreign key to users table)',
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 (Creation timestamp)',
    CONSTRAINT fk_tags_user      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_tags_name_user UNIQUE (name, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT '用户私有标签表，用于分类待办事项 (User-private tags for categorizing todos)';
