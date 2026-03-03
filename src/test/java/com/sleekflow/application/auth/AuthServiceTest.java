package com.sleekflow.application.auth;

import com.sleekflow.application.auth.impl.AuthServiceImpl;
import com.sleekflow.domain.user.User;
import com.sleekflow.infrastructure.config.JwtProperties;
import com.sleekflow.infrastructure.exception.DuplicateResourceException;
import com.sleekflow.infrastructure.exception.ResourceNotFoundException;
import com.sleekflow.infrastructure.persistence.user.UserRepository;
import com.sleekflow.infrastructure.security.JwtTokenProvider;
import com.sleekflow.interfaces.assembler.UserAssembler;
import com.sleekflow.interfaces.dto.request.LoginRequest;
import com.sleekflow.interfaces.dto.request.RegisterRequest;
import com.sleekflow.interfaces.dto.response.AuthResponse;
import com.sleekflow.interfaces.dto.response.UserSummaryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private JwtProperties jwtProperties;
    @Mock private UserAssembler userAssembler;

    @InjectMocks
    private AuthServiceImpl authService;

    // ── register ──────────────────────────────────────────────────────────────

    @Test
    void register_success_returnsAuthResponse() {
        RegisterRequest req = new RegisterRequest("alice@example.com", "alice", "password123");
        User saved = User.builder().id("u1").email("alice@example.com").username("alice")
                .passwordHash("hashed").build();

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(jwtTokenProvider.generateToken("u1", "alice@example.com")).thenReturn("jwt-token");
        when(jwtProperties.getExpirationMs()).thenReturn(86400000L);
        when(userAssembler.toSummary(saved))
                .thenReturn(new UserSummaryResponse("u1", "alice", "alice@example.com"));

        AuthResponse response = authService.register(req);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(86400L);
        assertThat(response.getUser().getId()).isEqualTo("u1");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_duplicateEmail_throwsDuplicateResourceException() {
        RegisterRequest req = new RegisterRequest("alice@example.com", "alice", "password123");
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(DuplicateResourceException.class);

        verify(userRepository, never()).save(any());
    }

    // ── login ─────────────────────────────────────────────────────────────────

    @Test
    void login_success_returnsAuthResponse() {
        LoginRequest req = new LoginRequest("alice@example.com", "password123");
        User user = User.builder().id("u1").email("alice@example.com").username("alice")
                .passwordHash("hashed").build();

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(jwtTokenProvider.generateToken("u1", "alice@example.com")).thenReturn("jwt-token");
        when(jwtProperties.getExpirationMs()).thenReturn(86400000L);
        when(userAssembler.toSummary(user))
                .thenReturn(new UserSummaryResponse("u1", "alice", "alice@example.com"));

        AuthResponse response = authService.login(req);

        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

    @Test
    void login_userNotFound_throwsBadCredentialsException() {
        LoginRequest req = new LoginRequest("nobody@example.com", "password123");
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void login_wrongPassword_throwsBadCredentialsException() {
        LoginRequest req = new LoginRequest("alice@example.com", "wrongpass");
        User user = User.builder().id("u1").email("alice@example.com").passwordHash("hashed").build();

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }

    /**
     * 测试登录安全：账号不存在和密码错误返回相同的异常类型
     * <p>
     * Test login security: same exception type for non-existent account and wrong password
     * </p>
     * <p>
     * 验证无论账号是否存在或密码是否错误，都抛出 BadCredentialsException 且错误消息相同。
     * </p>
     * <p>
     * Verifies that whether the account exists or the password is wrong, BadCredentialsException is thrown
     * with the same error message (prevents account enumeration).
     * </p>
     */
    @Test
    void login_security_sameExceptionForBothScenarios() {
        LoginRequest req1 = new LoginRequest("nobody@example.com", "password123");
        LoginRequest req2 = new LoginRequest("alice@example.com", "wrongpass");
        User user = User.builder().id("u1").email("alice@example.com").passwordHash("hashed").build();

        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Both scenarios should throw BadCredentialsException with same message
        Throwable ex1 = catchThrowable(() -> authService.login(req1));
        Throwable ex2 = catchThrowable(() -> authService.login(req2));

        assertThat(ex1).isInstanceOf(BadCredentialsException.class);
        assertThat(ex2).isInstanceOf(BadCredentialsException.class);
        assertThat(ex1.getMessage()).isEqualTo(ex2.getMessage());
    }
}
