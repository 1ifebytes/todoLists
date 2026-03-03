-- V7: Team membership with role-based access.
-- role: 'ADMIN' (can manage team and members) | 'MEMBER' (read-only access to team).
-- uq_tm_team_user: a user can only appear once per team.
-- Business rule enforced in TeamServiceImpl: the last ADMIN of a team cannot be removed.
CREATE TABLE team_members (
    id        VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '团队成员记录唯一标识，UUID格式 (Team member record unique identifier, UUID format)',
    team_id   VARCHAR(36) NOT NULL COMMENT '团队ID，外键关联teams表 (Team ID, foreign key to teams table)',
    user_id   VARCHAR(36) NOT NULL COMMENT '用户ID，外键关联users表 (User ID, foreign key to users table)',
    role      VARCHAR(20) NOT NULL DEFAULT 'MEMBER' COMMENT '角色：ADMIN(管理员) | MEMBER(成员) (Role: ADMIN(can manage) | MEMBER(read-only))',
    joined_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入团队时间 (Team join timestamp)',
    CONSTRAINT fk_tm_team      FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
    CONSTRAINT fk_tm_user      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_tm_team_user UNIQUE (team_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT '团队成员表，记录用户在团队中的角色和加入时间 (Team membership table with role-based access)';
