package com.sleekflow.infrastructure.persistence.team;

import com.sleekflow.domain.team.TeamMember;
import com.sleekflow.domain.team.enums.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 团队成员仓储接口
 * <p>
 * Team Member Repository Interface
 * </p>
 * <p>
 * 提供 TeamMember 实体的数据库访问操作。
 * </p>
 * <p>
 * Provides database access operations for the TeamMember entity.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, String> {

    /**
     * 根据团队 ID 和用户 ID 查找团队成员
     * <p>
     * Find team member by team ID and user ID
     * </p>
     *
     * @param teamId 团队 ID / Team ID
     * @param userId 用户 ID / User ID
     * @return 团队成员对象的 Optional 包装 / Optional wrapping the team member object
     */
    Optional<TeamMember> findByTeamIdAndUserId(String teamId, String userId);

    /**
     * 查找团队的所有成员
     * <p>
     * Find all members of a team
     * </p>
     *
     * @param teamId 团队 ID / Team ID
     * @return 团队成员列表 / List of team members
     */
    List<TeamMember> findAllByTeamId(String teamId);

    /**
     * 统计团队中指定角色的成员数量
     * <p>
     * Count members with a specific role in a team
     * </p>
     * <p>
     * 用于防止移除团队的最后一个 ADMIN。
     * </p>
     * <p>
     * Used to guard against removing the last ADMIN from a team.
     * </p>
     *
     * @param teamId 团队 ID / Team ID
     * @param role 团队角色 / Team role
     * @return 成员数量 / Count of members
     */
    long countByTeamIdAndRole(String teamId, TeamRole role);

    /**
     * 检查用户是否已是团队成员
     * <p>
     * Check if user is already a team member
     * </p>
     *
     * @param teamId 团队 ID / Team ID
     * @param userId 用户 ID / User ID
     * @return true 如果用户已是成员，否则 false / true if user is already a member, false otherwise
     */
    boolean existsByTeamIdAndUserId(String teamId, String userId);

    /**
     * 从团队中移除成员
     * <p>
     * Remove member from team
     * </p>
     *
     * @param teamId 团队 ID / Team ID
     * @param userId 用户 ID / User ID
     */
    void deleteByTeamIdAndUserId(String teamId, String userId);
}
