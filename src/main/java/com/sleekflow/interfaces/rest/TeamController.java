package com.sleekflow.interfaces.rest;

import com.sleekflow.application.team.ITeamService;
import com.sleekflow.interfaces.dto.request.AddTeamMemberRequest;
import com.sleekflow.interfaces.dto.request.CreateTeamRequest;
import com.sleekflow.interfaces.dto.request.UpdateTeamRequest;
import com.sleekflow.interfaces.dto.response.ApiResponse;
import com.sleekflow.interfaces.dto.response.TeamResponse;
import com.sleekflow.interfaces.dto.response.UserSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 团队控制器
 * <p>
 * Team Controller
 * </p>
 * <p>
 * 提供团队（Team）管理的 REST API 端点。
 * </p>
 * <p>
 * Provides REST API endpoints for Team management.
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
 * 待办事项分享通过 TodoController 的分享端点单独处理。
 * </p>
 * <p>
 * Todo sharing is handled separately via TodoController's share endpoints.
 * </p>
 * <p>
 * <b>端点（Endpoints）：</b></p>
 * <ul>
 *   <li>GET /api/v1/teams - 查询用户所属的团队 / List user's teams</li>
 *   <li>POST /api/v1/teams - 创建团队（调用者成为 ADMIN）/ Create team (caller becomes ADMIN)</li>
 *   <li>GET /api/v1/teams/{id} - 查询团队详情（仅成员）/ Get team details (members only)</li>
 *   <li>PUT /api/v1/teams/{id} - 更新团队（仅 ADMIN）/ Update team (admin only)</li>
 *   <li>DELETE /api/v1/teams/{id} - 删除团队（仅 ADMIN）/ Delete team (admin only)</li>
 *   <li>GET /api/v1/teams/{id}/members - 查询团队成员（任何成员）/ List team members (any member)</li>
 *   <li>POST /api/v1/teams/{id}/members - 添加成员（仅 ADMIN）/ Add member (admin only)</li>
 *   <li>DELETE /api/v1/teams/{id}/members/{userId} - 移除成员（仅 ADMIN，不能移除最后一名 ADMIN）/ Remove member (admin only, cannot remove last admin)</li>
 * </ul>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Teams", description = "Team management")
public class TeamController {

    private final ITeamService teamService;

    /**
     * 查询用户所属的所有团队
     * <p>
     * List all teams the authenticated user belongs to
     * </p>
     * <p>
     * 返回调用者所属的所有团队（任何角色），按创建时间倒序排列。
     * </p>
     * <p>
     * Returns all teams the caller belongs to (any role), sorted by creation time in descending order.
     * </p>
     *
     * @return 包含团队列表的响应 / Response containing list of teams
     */
    @Operation(summary = "List all teams the authenticated user belongs to")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TeamResponse>>> listMyTeams() {
        return ResponseEntity.ok(ApiResponse.success(teamService.listMyTeams()));
    }

    /**
     * 创建新团队
     * <p>
     * Create a new team
     * </p>
     * <p>
     * 创建新团队，调用者自动注册为 ADMIN 角色。
     * </p>
     * <p>
     * Creates a new team and auto-enrolls the caller as ADMIN.
     * </p>
     *
     * @param request 创建团队请求体 / Request body containing team name and description
     * @return 包含创建的团队信息的响应，HTTP 201 状态码 / Response containing created team with HTTP 201 status
     */
    @Operation(summary = "Create a new team (caller becomes ADMIN)")
    @PostMapping
    public ResponseEntity<ApiResponse<TeamResponse>> createTeam(
            @Valid @RequestBody CreateTeamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(teamService.createTeam(request)));
    }

    /**
     * 查询团队详情
     * <p>
     * Get a team by ID
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
     * @param id 团队 ID / Team ID
     * @return 包含团队详细信息的响应 / Response containing team details
     */
    @Operation(summary = "Get a team by ID (members only)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeamResponse>> getTeam(
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(teamService.getTeam(id)));
    }

    /**
     * 更新团队信息
     * <p>
     * Update a team
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
     * @param id 团队 ID / Team ID
     * @param request 更新团队请求体 / Request body containing fields to update
     * @return 包含更新后团队信息的响应 / Response containing updated team details
     */
    @Operation(summary = "Update a team (admin only)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TeamResponse>> updateTeam(
            @PathVariable String id,
            @Valid @RequestBody UpdateTeamRequest request) {
        return ResponseEntity.ok(ApiResponse.success(teamService.updateTeam(id, request)));
    }

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
     * @param id 团队 ID / Team ID
     * @return HTTP 204 无内容响应 / HTTP 204 no content response
     */
    @Operation(summary = "Delete a team (admin only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(
            @PathVariable String id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 查询团队成员列表
     * <p>
     * List all members of a team
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
     * @param id 团队 ID / Team ID
     * @return 包含成员列表的响应 / Response containing list of members
     */
    @Operation(summary = "List all members of a team (any member)")
    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<UserSummaryResponse>>> listMembers(
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(teamService.listMembers(id)));
    }

    /**
     * 添加团队成员
     * <p>
     * Add a member to a team
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
     * @param id 团队 ID / Team ID
     * @param request 添加团队成员请求体 / Request body containing user ID and role
     * @return HTTP 201 无内容响应 / HTTP 201 no content response
     */
    @Operation(summary = "Add a member to a team (admin only)")
    @PostMapping("/{id}/members")
    public ResponseEntity<Void> addMember(
            @PathVariable String id,
            @Valid @RequestBody AddTeamMemberRequest request) {
        teamService.addMember(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 移除团队成员
     * <p>
     * Remove a member from a team
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
     * @param id 团队 ID / Team ID
     * @param userId 要移除的成员用户 ID / ID of the member to remove
     * @return HTTP 204 无内容响应 / HTTP 204 no content response
     */
    @Operation(summary = "Remove a member from a team (admin only, cannot remove last admin)")
    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable String id,
            @PathVariable String userId) {
        teamService.removeMember(id, userId);
        return ResponseEntity.noContent().build();
    }
}
