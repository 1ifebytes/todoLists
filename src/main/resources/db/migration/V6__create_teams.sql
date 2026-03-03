-- V6: Teams for grouping users.
-- Teams are independent of todos: membership here does NOT automatically grant
--   todo access. Todo sharing is handled separately via todo_permissions.
-- created_by: nullable FK, SET NULL if the creator's account is deleted,
--   so the team is not lost when the admin leaves.
CREATE TABLE teams (
    id          VARCHAR(36)  NOT NULL PRIMARY KEY COMMENT '团队唯一标识，UUID格式 (Team unique identifier, UUID format)',
    name        VARCHAR(255) NOT NULL COMMENT '团队名称 (Team name)',
    description TEXT         COMMENT '团队描述，支持长文本 (Team description, supports long text)',
    created_by  VARCHAR(36) COMMENT '创建者用户ID，外键关联users表，可空 (Creator user ID, foreign key to users table, nullable)',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 (Creation timestamp)',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间，自动更新 (Last update timestamp, auto-updated)',
    CONSTRAINT fk_teams_creator FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT '团队表，用于用户分组 (Teams table for grouping users, independent from todo access)';
