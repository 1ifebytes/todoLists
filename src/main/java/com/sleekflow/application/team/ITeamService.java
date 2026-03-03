package com.sleekflow.application.team;

import com.sleekflow.interfaces.dto.request.AddTeamMemberRequest;
import com.sleekflow.interfaces.dto.request.CreateTeamRequest;
import com.sleekflow.interfaces.dto.request.UpdateTeamRequest;
import com.sleekflow.interfaces.dto.response.TeamResponse;
import com.sleekflow.interfaces.dto.response.UserSummaryResponse;

import java.util.List;

/**
 * 团队服务接口
 * <p>
 * Team Service Interface
 * </p>
 * <p>
 * 定义团队（Team）相关的业务操作，包括创建、查询、更新、删除团队以及团队成员管理。
 * </p>
 * <p>
 * Defines business operations for teams, including creation, querying, updating, deletion,
 * and team member management.
 * </p>
 * <p>
 * <b>重要说明（Important Note）：</b></p>
 * <p>
 * 团队独立于待办事项。团队成员身份不会授予待办事项访问权限。
 * </p>
 * <p>
 * Teams are independent of todos. Team membership does NOT grant todo access.
 * </p>
 * <p>
 * 待办事项分享通过 TodoPermission 单独处理。
 * </p>
 * <p>
 * Todo sharing is handled separately via TodoPermission.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
public interface ITeamService {

    /**
     * 创建团队
     * <p>
     * Create a team
     * </p>
     * <p>
     * 创建新团队，调用者自动注册为 ADMIN 角色。
     * </p>
     * <p>
     * Creates a new team and auto-enrolls the caller as ADMIN.
     * </p>
     *
     * @param request 创建团队请求 / Request containing team name and description
     * @return 创建的团队响应 / Created team response
     */
    TeamResponse createTeam(CreateTeamRequest request);

    /**
     * 查询用户所属的所有团队
     * <p>
     * List all teams the user belongs to
     * </p>
     * <p>
     * 返回调用者所属的所有团队（任何角色），按创建时间倒序排列。
     * </p>
     * <p>
     * Returns all teams the caller belongs to (any role), sorted by creation time in descending order.
     * </p>
     *
     * @return 团队列表响应 / List of teams response
     */
    List<TeamResponse> listMyTeams();

    /**
     * 查询团队详情
     * <p>
     * Get team details
     * </p>
     * <p>
     * 返回指定团队的详细信息。
     * 调用者必须是团队成员才能查看团队详情。
     * </p>
     * <p>
     * Returns detailed information of the specified team.
     * Caller must be a team member to view team details.
     * </p>
     *
     * @param teamId 团队 ID / Team ID
     * @return 团队响应 / Team response
     */
    TeamResponse getTeam(String teamId);

    /**
     * 更新团队信息
     * <p>
     * Update team information
     * </p>
     * <p>
     * 更新团队的名称和描述。
     * 只有团队 ADMIN 可以执行此操作。
     * </p>
     * <p>
     * Updates the team name and description.
     * Only team ADMIN can perform this operation.
     * </p>
     *
     * @param teamId 团队 ID / Team ID
     * @param request 更新团队请求 / Request containing fields to update
     * @return 更新后的团队响应 / Updated team response
     */
    TeamResponse updateTeam(String teamId, UpdateTeamRequest request);

    /**
     * 删除团队
     * <p>
     * Delete a team
     * </p>
     * <p>
     * 删除指定团队及其所有成员记录。
     * 只有团队 ADMIN 可以执行此操作。
     * </p>
     * <p>
     * Deletes the specified team and all its member records.
     * Only team ADMIN can perform this operation.
     * </p>
     *
     * @param teamId 团队 ID / Team ID
     */
    void deleteTeam(String teamId);

    /**
     * 添加团队成员
     * <p>
     * Add a member to the team
     * </p>
     * <p>
     * 将指定用户添加到团队，并授予指定的角色（ADMIN 或 MEMBER）。
     * 只有团队 ADMIN 可以执行此操作。
     * </p>
     * <p>
     * Adds a user to the team with the specified role (ADMIN or MEMBER).
     * Only team ADMIN can perform this operation.
     * </p>
     *
     * @param teamId 团队 ID / Team ID
     * @param request 添加团队成员请求 / Request containing user ID and role
     */
    void addMember(String teamId, AddTeamMemberRequest request);

    /**
     * 移除团队成员
     * <p>
     * Remove a member from the team
     * </p>
    * <p>
     * 从团队中移除指定成员。
     * 只有团队 ADMIN 可以执行此操作。
     * 业务规则：不能移除团队的最后一名 ADMIN。
     * </p>
     * <p>
     * Removes the specified member from the team.
     * Only team ADMIN can perform this operation.
     * Business rule: Cannot remove the last ADMIN of a team.
     * </p>
     *
     * @param teamId 团队 ID / Team ID
     * @param targetUserId 要移除的成员用户 ID / ID of the member to remove
     */
    void removeMember(String teamId, String targetUserId);

    /**
     * 查询团队成员列表
     * <p>
     * List team members
     * </p>
     * <p>
     * 返回指定团队的所有成员。
     * 任何团队成员都可以查询其他成员。
     * </p>
     * <p>
     * Returns all members of the specified team.
     * Any team member can list other members.
     * </p>
     *
     * @param teamId 团队 ID / Team ID
     * @return 成员列表响应 / List of members response
     */
    List<UserSummaryResponse> listMembers(String teamId);
}
