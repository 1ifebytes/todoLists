package com.sleekflow.domain.team.enums;

/**
 * 团队角色枚举
 * <p>
 * Team Role Enumeration
 * </p>
 * <p>
 * 定义团队成员的角色级别，用于团队管理。
 * 业务规则：团队的最后一个 ADMIN 不能被移除（在 TeamServiceImpl 中强制执行）。
 * </p>
 * <p>
 * Defines the role levels of team members, used for team management.
 * Business rule: The last ADMIN of a team cannot be removed (enforced in TeamServiceImpl).
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
public enum TeamRole {

    /**
     * 管理员 - Administrator
     * <p>
     * 团队管理员，拥有全部团队管理权限（添加/移除成员、更新团队信息等）
     * </p>
     * <p>
     * Team administrator with full team management permissions (add/remove members, update team info, etc.)
     * </p>
     */
    ADMIN,

    /**
     * 普通成员 - Member
     * <p>
     * 团队普通成员，只能查看团队信息和成员列表，无管理权限
     * </p>
     * <p>
     * Regular team member, can only view team information and member list, no management permissions
     * </p>
     */
    MEMBER
}
