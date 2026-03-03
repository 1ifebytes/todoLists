package com.sleekflow.application.team;

import com.sleekflow.application.team.impl.TeamServiceImpl;
import com.sleekflow.domain.team.Team;
import com.sleekflow.domain.team.TeamMember;
import com.sleekflow.domain.team.enums.TeamRole;
import com.sleekflow.domain.user.User;
import com.sleekflow.infrastructure.exception.DuplicateResourceException;
import com.sleekflow.infrastructure.exception.ForbiddenException;
import com.sleekflow.infrastructure.exception.ResourceNotFoundException;
import com.sleekflow.infrastructure.persistence.team.TeamMemberRepository;
import com.sleekflow.infrastructure.persistence.team.TeamRepository;
import com.sleekflow.infrastructure.persistence.user.UserRepository;
import com.sleekflow.infrastructure.security.UserContext;
import com.sleekflow.interfaces.assembler.TeamAssembler;
import com.sleekflow.interfaces.assembler.UserAssembler;
import com.sleekflow.interfaces.dto.request.AddTeamMemberRequest;
import com.sleekflow.interfaces.dto.request.CreateTeamRequest;
import com.sleekflow.interfaces.dto.request.UpdateTeamRequest;
import com.sleekflow.interfaces.dto.response.TeamResponse;
import com.sleekflow.interfaces.dto.response.UserSummaryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock private TeamRepository teamRepository;
    @Mock private TeamMemberRepository teamMemberRepository;
    @Mock private UserRepository userRepository;
    @Mock private TeamAssembler teamAssembler;
    @Mock private UserAssembler userAssembler;

    @InjectMocks
    private TeamServiceImpl teamService;

    private final User alice = User.builder().id("u1").email("alice@example.com").username("alice").build();
    private final User bob   = User.builder().id("u2").email("bob@example.com").username("bob").build();

    private Team buildTeam(String id) {
        return Team.builder().id(id).name("Alpha").createdBy(alice).members(new ArrayList<>()).build();
    }

    private TeamResponse buildTeamResponse(String id) {
        TeamResponse r = new TeamResponse();
        r.setId(id);
        r.setName("Alpha");
        return r;
    }

    // ── createTeam ────────────────────────────────────────────────────────────

    @Test
    void createTeam_success_enrollsCallerAsAdminAndReturnsMemberCount1() {
        CreateTeamRequest req = new CreateTeamRequest("Alpha", "Our team");
        Team saved = buildTeam("team1");
        TeamResponse mapped = buildTeamResponse("team1");

        when(userRepository.findById("u1")).thenReturn(Optional.of(alice));
        when(teamRepository.save(any(Team.class))).thenReturn(saved);
        when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(
                TeamMember.builder().team(saved).user(alice).role(TeamRole.ADMIN).build());
        when(teamAssembler.toResponse(saved)).thenReturn(mapped);

        UserContext.setCurrentUserId("u1");
        try {
            TeamResponse response = teamService.createTeam(req);

            assertThat(response.getMemberCount()).isEqualTo(1);
            assertThat(response.getMyRole()).isEqualTo(TeamRole.ADMIN);
            verify(teamMemberRepository).save(any(TeamMember.class));
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void createTeam_callerNotFound_throwsResourceNotFoundException() {
        when(userRepository.findById("u99")).thenReturn(Optional.empty());

        UserContext.setCurrentUserId("u99");
        try {
            assertThatThrownBy(() -> teamService.createTeam(new CreateTeamRequest("Alpha", null)))
                    .isInstanceOf(ResourceNotFoundException.class);
        } finally {
            UserContext.clear();
        }
    }

    // ── getTeam ───────────────────────────────────────────────────────────────

    @Test
    void getTeam_asMember_returnsTeamWithMyRole() {
        Team team = buildTeam("team1");
        TeamMember member = TeamMember.builder().team(team).user(alice).role(TeamRole.ADMIN).build();
        TeamResponse mapped = buildTeamResponse("team1");

        when(teamRepository.findById("team1")).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndUserId("team1", "u1")).thenReturn(Optional.of(member));
        when(teamAssembler.toResponse(team)).thenReturn(mapped);

        UserContext.setCurrentUserId("u1");
        try {
            TeamResponse response = teamService.getTeam("team1");

            assertThat(response.getMyRole()).isEqualTo(TeamRole.ADMIN);
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void getTeam_notMember_throwsForbiddenException() {
        Team team = buildTeam("team1");
        when(teamRepository.findById("team1")).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndUserId("team1", "u2")).thenReturn(Optional.empty());

        UserContext.setCurrentUserId("u2");
        try {
            assertThatThrownBy(() -> teamService.getTeam("team1"))
                    .isInstanceOf(ForbiddenException.class);
        } finally {
            UserContext.clear();
        }
    }

    // ── updateTeam ────────────────────────────────────────────────────────────

    @Test
    void updateTeam_asAdmin_updatesName() {
        Team team = buildTeam("team1");
        TeamMember adminMember = TeamMember.builder().team(team).user(alice).role(TeamRole.ADMIN).build();
        UpdateTeamRequest req = new UpdateTeamRequest("Beta", null);
        TeamResponse mapped = buildTeamResponse("team1");

        when(teamRepository.findById("team1")).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndUserId("team1", "u1")).thenReturn(Optional.of(adminMember));
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(teamAssembler.toResponse(team)).thenReturn(mapped);

        UserContext.setCurrentUserId("u1");
        try {
            teamService.updateTeam("team1", req);

            assertThat(team.getName()).isEqualTo("Beta");
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void updateTeam_asMember_throwsForbiddenException() {
        Team team = buildTeam("team1");
        TeamMember member = TeamMember.builder().team(team).user(bob).role(TeamRole.MEMBER).build();

        when(teamRepository.findById("team1")).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndUserId("team1", "u2")).thenReturn(Optional.of(member));

        UserContext.setCurrentUserId("u2");
        try {
            assertThatThrownBy(() -> teamService.updateTeam("team1", new UpdateTeamRequest("x", null)))
                    .isInstanceOf(ForbiddenException.class);
        } finally {
            UserContext.clear();
        }
    }

    // ── addMember ─────────────────────────────────────────────────────────────

    @Test
    void addMember_success_savesTeamMember() {
        Team team = buildTeam("team1");
        TeamMember adminMember = TeamMember.builder().team(team).user(alice).role(TeamRole.ADMIN).build();
        AddTeamMemberRequest req = new AddTeamMemberRequest("u2", TeamRole.MEMBER);

        when(teamRepository.findById("team1")).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndUserId("team1", "u1")).thenReturn(Optional.of(adminMember));
        when(teamMemberRepository.existsByTeamIdAndUserId("team1", "u2")).thenReturn(false);
        when(userRepository.findById("u2")).thenReturn(Optional.of(bob));

        UserContext.setCurrentUserId("u1");
        try {
            teamService.addMember("team1", req);

            verify(teamMemberRepository).save(any(TeamMember.class));
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void addMember_alreadyMember_throwsDuplicateResourceException() {
        Team team = buildTeam("team1");
        TeamMember adminMember = TeamMember.builder().team(team).user(alice).role(TeamRole.ADMIN).build();

        when(teamRepository.findById("team1")).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndUserId("team1", "u1")).thenReturn(Optional.of(adminMember));
        when(teamMemberRepository.existsByTeamIdAndUserId("team1", "u2")).thenReturn(true);

        UserContext.setCurrentUserId("u1");
        try {
            assertThatThrownBy(() -> teamService.addMember("team1", new AddTeamMemberRequest("u2", TeamRole.MEMBER)))
                    .isInstanceOf(DuplicateResourceException.class);
        } finally {
            UserContext.clear();
        }
    }

    // ── removeMember ──────────────────────────────────────────────────────────

    @Test
    void removeMember_success_deletesMember() {
        Team team = buildTeam("team1");
        TeamMember adminMember = TeamMember.builder().team(team).user(alice).role(TeamRole.ADMIN).build();
        TeamMember bobMember = TeamMember.builder().team(team).user(bob).role(TeamRole.MEMBER).build();

        when(teamRepository.findById("team1")).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndUserId("team1", "u1")).thenReturn(Optional.of(adminMember));
        when(teamMemberRepository.findByTeamIdAndUserId("team1", "u2")).thenReturn(Optional.of(bobMember));

        UserContext.setCurrentUserId("u1");
        try {
            teamService.removeMember("team1", "u2");

            verify(teamMemberRepository).deleteByTeamIdAndUserId("team1", "u2");
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void removeMember_lastAdmin_throwsForbiddenException() {
        Team team = buildTeam("team1");
        TeamMember adminMember = TeamMember.builder().team(team).user(alice).role(TeamRole.ADMIN).build();

        when(teamRepository.findById("team1")).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndUserId("team1", "u1")).thenReturn(Optional.of(adminMember));
        when(teamMemberRepository.findByTeamIdAndUserId("team1", "u1")).thenReturn(Optional.of(adminMember));
        // Alice is the only admin
        when(teamMemberRepository.countByTeamIdAndRole("team1", TeamRole.ADMIN)).thenReturn(1L);

        UserContext.setCurrentUserId("u1");
        try {
            assertThatThrownBy(() -> teamService.removeMember("team1", "u1"))
                    .isInstanceOf(ForbiddenException.class);
        } finally {
            UserContext.clear();
        }
    }

    // ── listMembers ───────────────────────────────────────────────────────────

    @Test
    void listMembers_asMember_returnsUserSummaries() {
        Team team = buildTeam("team1");
        TeamMember aliceMember = TeamMember.builder().team(team).user(alice).role(TeamRole.ADMIN).build();

        when(teamRepository.findById("team1")).thenReturn(Optional.of(team));
        when(teamMemberRepository.existsByTeamIdAndUserId("team1", "u1")).thenReturn(true);
        when(teamMemberRepository.findAllByTeamId("team1")).thenReturn(List.of(aliceMember));
        when(userAssembler.toSummary(alice)).thenReturn(
                new UserSummaryResponse("u1", "alice", "alice@example.com"));

        UserContext.setCurrentUserId("u1");
        try {
            List<UserSummaryResponse> result = teamService.listMembers("team1");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo("u1");
        } finally {
            UserContext.clear();
        }
    }
}
