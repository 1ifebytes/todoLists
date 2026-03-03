package com.sleekflow.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekflow.application.team.ITeamService;
import com.sleekflow.domain.team.enums.TeamRole;
import com.sleekflow.infrastructure.exception.ForbiddenException;
import com.sleekflow.infrastructure.exception.GlobalExceptionHandler;
import com.sleekflow.infrastructure.exception.ResourceNotFoundException;
import com.sleekflow.infrastructure.security.UserContext;
import com.sleekflow.interfaces.dto.request.AddTeamMemberRequest;
import com.sleekflow.interfaces.dto.request.CreateTeamRequest;
import com.sleekflow.interfaces.dto.request.UpdateTeamRequest;
import com.sleekflow.interfaces.dto.response.TeamResponse;
import com.sleekflow.interfaces.dto.response.UserSummaryResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TeamControllerTest {

    @Mock ITeamService teamService;
    @InjectMocks TeamController teamController;

    MockMvc mockMvc;
    final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        UserContext.setCurrentUserId("u1");
        mockMvc = MockMvcBuilders.standaloneSetup(teamController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    private TeamResponse buildTeamResponse() {
        TeamResponse r = new TeamResponse();
        r.setId("team1");
        r.setName("Alpha");
        r.setMemberCount(1);
        r.setMyRole(TeamRole.ADMIN);
        r.setCreatedAt(LocalDateTime.now());
        r.setUpdatedAt(LocalDateTime.now());
        return r;
    }

    // ── GET /teams ────────────────────────────────────────────────────────────

    @Test
    void listMyTeams_authenticated_returns200() throws Exception {
        when(teamService.listMyTeams()).thenReturn(List.of(buildTeamResponse()));

        mockMvc.perform(get("/api/v1/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].myRole").value("ADMIN"));
    }

    // ── POST /teams ───────────────────────────────────────────────────────────

    @Test
    void createTeam_validRequest_returns201() throws Exception {
        CreateTeamRequest req = new CreateTeamRequest("Alpha", "Our team");
        when(teamService.createTeam(any())).thenReturn(buildTeamResponse());

        mockMvc.perform(post("/api/v1/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value("team1"))
                .andExpect(jsonPath("$.data.memberCount").value(1));
    }

    @Test
    void createTeam_blankName_returns400() throws Exception {
        CreateTeamRequest req = new CreateTeamRequest("", null);

        mockMvc.perform(post("/api/v1/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ── GET /teams/{id} ───────────────────────────────────────────────────────

    @Test
    void getTeam_asMember_returns200() throws Exception {
        when(teamService.getTeam("team1")).thenReturn(buildTeamResponse());

        mockMvc.perform(get("/api/v1/teams/team1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("team1"));
    }

    @Test
    void getTeam_notMember_returns403() throws Exception {
        when(teamService.getTeam("team1"))
                .thenThrow(new ForbiddenException("You are not a member of this team"));

        mockMvc.perform(get("/api/v1/teams/team1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTeam_notFound_returns404() throws Exception {
        when(teamService.getTeam("team99"))
                .thenThrow(new ResourceNotFoundException("Team", "team99"));

        mockMvc.perform(get("/api/v1/teams/team99"))
                .andExpect(status().isNotFound());
    }

    // ── PUT /teams/{id} ───────────────────────────────────────────────────────

    @Test
    void updateTeam_asAdmin_returns200() throws Exception {
        UpdateTeamRequest req = new UpdateTeamRequest("Beta", null);
        TeamResponse updated = buildTeamResponse();
        updated.setName("Beta");
        when(teamService.updateTeam(eq("team1"), any())).thenReturn(updated);

        mockMvc.perform(put("/api/v1/teams/team1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Beta"));
    }

    @Test
    void updateTeam_asMember_returns403() throws Exception {
        UpdateTeamRequest req = new UpdateTeamRequest("x", null);
        when(teamService.updateTeam(eq("team1"), any()))
                .thenThrow(new ForbiddenException("Only team admins can perform this action"));

        mockMvc.perform(put("/api/v1/teams/team1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    // ── DELETE /teams/{id} ────────────────────────────────────────────────────

    @Test
    void deleteTeam_asAdmin_returns204() throws Exception {
        doNothing().when(teamService).deleteTeam("team1");

        mockMvc.perform(delete("/api/v1/teams/team1"))
                .andExpect(status().isNoContent());
    }

    // ── GET /teams/{id}/members ───────────────────────────────────────────────

    @Test
    void listMembers_asMember_returns200() throws Exception {
        when(teamService.listMembers("team1")).thenReturn(List.of(
                new UserSummaryResponse("u1", "alice", "alice@example.com")));

        mockMvc.perform(get("/api/v1/teams/team1/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    // ── POST /teams/{id}/members ──────────────────────────────────────────────

    @Test
    void addMember_asAdmin_returns201() throws Exception {
        AddTeamMemberRequest req = new AddTeamMemberRequest("u2", TeamRole.MEMBER);
        doNothing().when(teamService).addMember(eq("team1"), any());

        mockMvc.perform(post("/api/v1/teams/team1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    // ── DELETE /teams/{id}/members/{userId} ───────────────────────────────────

    @Test
    void removeMember_asAdmin_returns204() throws Exception {
        doNothing().when(teamService).removeMember("team1", "u2");

        mockMvc.perform(delete("/api/v1/teams/team1/members/u2"))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeMember_lastAdmin_returns403() throws Exception {
        doThrow(new ForbiddenException("Cannot remove the last admin from a team"))
                .when(teamService).removeMember("team1", "u1");

        mockMvc.perform(delete("/api/v1/teams/team1/members/u1"))
                .andExpect(status().isForbidden());
    }
}
