package com.sleekflow.application.team.impl;

import com.sleekflow.application.team.ITeamService;
import com.sleekflow.domain.team.Team;
import com.sleekflow.domain.team.TeamMember;
import com.sleekflow.domain.team.enums.TeamRole;
import com.sleekflow.domain.user.User;
import com.sleekflow.infrastructure.security.UserContext;
import com.sleekflow.infrastructure.exception.DuplicateResourceException;
import com.sleekflow.infrastructure.exception.ForbiddenException;
import com.sleekflow.infrastructure.exception.ResourceNotFoundException;
import com.sleekflow.infrastructure.persistence.team.TeamMemberRepository;
import com.sleekflow.infrastructure.persistence.team.TeamRepository;
import com.sleekflow.infrastructure.persistence.user.UserRepository;
import com.sleekflow.interfaces.assembler.TeamAssembler;
import com.sleekflow.interfaces.assembler.UserAssembler;
import com.sleekflow.interfaces.dto.request.AddTeamMemberRequest;
import com.sleekflow.interfaces.dto.request.CreateTeamRequest;
import com.sleekflow.interfaces.dto.request.UpdateTeamRequest;
import com.sleekflow.interfaces.dto.response.TeamResponse;
import com.sleekflow.interfaces.dto.response.UserSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 团队服务实现
 * <p>
 * Team Service Implementation
 * </p>
 * <p>
 * 实现团队（Team）管理功能，包括团队创建、成员管理、权限控制等。
 * </p>
 * <p>
 * Implements team management functionality, including team creation,
 * member management, and permission control.
 * </p>
 * <p>
 * <b>团队角色（Team Roles）：</b></p>
 * <ul>
 *   <li>ADMIN（管理员）：完全控制权，可添加/移除成员、更新团队信息、删除团队</li>
 *   <li>MEMBER（成员）：只读访问，可查看团队信息和成员列表</li>
 * </ul>
 * <p>
 * <b>业务规则（Business Rules）：</b></p>
 * <ul>
 *   <li>创建团队的用户自动成为 ADMIN</li>
 *   <li>不能移除团队的最后一名 ADMIN（防止团队无管理）</li>
 *   <li>团队成员身份与待办事项权限独立（团队不授予 Todo 访问权）</li>
 * </ul>
 * <p>
 * <b>Team Independence：</b> Team membership does NOT grant todo access.
 * Todo sharing is handled separately via {@link com.sleekflow.application.todo.ITodoService#shareTodo}.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 * @see ITeamService
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TeamServiceImpl implements ITeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final TeamAssembler teamAssembler;
    private final UserAssembler userAssembler;

    @Override
    public TeamResponse createTeam(CreateTeamRequest request) {
        String callerId = UserContext.getCurrentUserId();
        User creator = userRepository.findById(callerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", callerId));

        Team team = Team.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdBy(creator)
                .members(new ArrayList<>())
                .build();

        team = teamRepository.save(team);

        TeamMember adminMember = TeamMember.builder()
                .team(team)
                .user(creator)
                .role(TeamRole.ADMIN)
                .build();
        teamMemberRepository.save(adminMember);

        TeamResponse response = teamAssembler.toResponse(team);
        response.setMemberCount(1);
        response.setMyRole(TeamRole.ADMIN);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamResponse> listMyTeams() {
        String callerId = UserContext.getCurrentUserId();
        return teamRepository.findAllByMemberUserId(callerId).stream()
                .map(team -> {
                    TeamResponse response = teamAssembler.toResponse(team);
                    TeamRole myRole = teamMemberRepository
                            .findByTeamIdAndUserId(team.getId(), callerId)
                            .map(TeamMember::getRole)
                            .orElse(TeamRole.MEMBER);
                    response.setMyRole(myRole);
                    return response;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TeamResponse getTeam(String teamId) {
        String callerId = UserContext.getCurrentUserId();
        Team team = findTeamOrThrow(teamId);
        TeamMember member = teamMemberRepository.findByTeamIdAndUserId(teamId, callerId)
                .orElseThrow(() -> new ForbiddenException("You are not a member of this team"));

        TeamResponse response = teamAssembler.toResponse(team);
        response.setMyRole(member.getRole());
        return response;
    }

    @Override
    public TeamResponse updateTeam(String teamId, UpdateTeamRequest request) {
        String callerId = UserContext.getCurrentUserId();
        Team team = findTeamOrThrow(teamId);
        TeamMember admin = requireAdmin(teamId, callerId);

        if (request.getName() != null)        team.setName(request.getName());
        if (request.getDescription() != null) team.setDescription(request.getDescription());

        team = teamRepository.save(team);

        TeamResponse response = teamAssembler.toResponse(team);
        response.setMyRole(admin.getRole());
        return response;
    }

    @Override
    public void deleteTeam(String teamId) {
        String callerId = UserContext.getCurrentUserId();
        Team team = findTeamOrThrow(teamId);
        requireAdmin(teamId, callerId);
        teamRepository.delete(team);
    }

    @Override
    public void addMember(String teamId, AddTeamMemberRequest request) {
        String callerId = UserContext.getCurrentUserId();
        Team team = findTeamOrThrow(teamId);
        requireAdmin(teamId, callerId);

        if (teamMemberRepository.existsByTeamIdAndUserId(teamId, request.getUserId())) {
            throw new DuplicateResourceException("User is already a member of this team");
        }

        User newUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        TeamMember member = TeamMember.builder()
                .team(team)
                .user(newUser)
                .role(request.getRole())
                .build();
        teamMemberRepository.save(member);
    }

    @Override
    public void removeMember(String teamId, String targetUserId) {
        String callerId = UserContext.getCurrentUserId();
        findTeamOrThrow(teamId);
        requireAdmin(teamId, callerId);

        TeamMember target = teamMemberRepository.findByTeamIdAndUserId(teamId, targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("TeamMember", "userId", targetUserId));

        // Guard: cannot remove the last ADMIN from a team.
        if (target.getRole() == TeamRole.ADMIN &&
                teamMemberRepository.countByTeamIdAndRole(teamId, TeamRole.ADMIN) <= 1) {
            throw new ForbiddenException("Cannot remove the last admin from a team");
        }

        teamMemberRepository.deleteByTeamIdAndUserId(teamId, targetUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryResponse> listMembers(String teamId) {
        String callerId = UserContext.getCurrentUserId();
        findTeamOrThrow(teamId);

        if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, callerId)) {
            throw new ForbiddenException("You are not a member of this team");
        }

        return teamMemberRepository.findAllByTeamId(teamId).stream()
                .map(m -> userAssembler.toSummary(m.getUser()))
                .toList();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Team findTeamOrThrow(String teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));
    }

    private TeamMember requireAdmin(String teamId, String callerId) {
        TeamMember member = teamMemberRepository.findByTeamIdAndUserId(teamId, callerId)
                .orElseThrow(() -> new ForbiddenException("You are not a member of this team"));

        if (member.getRole() != TeamRole.ADMIN) {
            throw new ForbiddenException("Only team admins can perform this action");
        }

        return member;
    }
}
