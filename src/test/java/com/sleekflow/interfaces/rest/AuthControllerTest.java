package com.sleekflow.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekflow.application.auth.IAuthService;
import com.sleekflow.infrastructure.exception.DuplicateResourceException;
import com.sleekflow.infrastructure.exception.GlobalExceptionHandler;
import com.sleekflow.interfaces.dto.request.LoginRequest;
import com.sleekflow.interfaces.dto.request.RegisterRequest;
import com.sleekflow.interfaces.dto.response.AuthResponse;
import com.sleekflow.interfaces.dto.response.UserSummaryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock IAuthService authService;
    @InjectMocks AuthController authController;

    MockMvc mockMvc;
    final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private final AuthResponse authResponse = new AuthResponse(
            "jwt-token", "Bearer", 86400L,
            new UserSummaryResponse("u1", "alice", "alice@example.com"));

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ── register ──────────────────────────────────────────────────────────────

    @Test
    void register_validRequest_returns201() throws Exception {
        RegisterRequest req = new RegisterRequest("alice@example.com", "alice", "password123");
        when(authService.register(any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("jwt-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }

    @Test
    void register_duplicateEmail_returns409() throws Exception {
        RegisterRequest req = new RegisterRequest("alice@example.com", "alice", "password123");
        when(authService.register(any())).thenThrow(new DuplicateResourceException("Email already registered"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void register_missingEmail_returns400() throws Exception {
        // email is blank → Bean Validation fails
        RegisterRequest req = new RegisterRequest("", "alice", "password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ── login ─────────────────────────────────────────────────────────────────

    @Test
    void login_validCredentials_returns200() throws Exception {
        LoginRequest req = new LoginRequest("alice@example.com", "password123");
        when(authService.login(any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("jwt-token"));
    }

    @Test
    void login_wrongPassword_returns401() throws Exception {
        LoginRequest req = new LoginRequest("alice@example.com", "wrongpass");
        when(authService.login(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }
}
