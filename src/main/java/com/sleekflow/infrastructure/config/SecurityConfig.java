package com.sleekflow.infrastructure.config;

import com.sleekflow.infrastructure.security.JwtAuthenticationFilter;
import com.sleekflow.infrastructure.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.LocalDateTime;

/**
 * Spring Security 配置类
 * <p>
 * Spring Security Configuration Class
 * </p>
 * <p>
 * 配置应用的认证和授权规则，使用 JWT 进行无状态认证。
 * </p>
 * <p>
 * Configures authentication and authorization rules for the application, using JWT for stateless authentication.
 * </p>
 * <p>
 * <b>主要配置（Key Configuration）：</b></p>
 * <ul>
 *   <li>禁用 CSRF（无状态 API）/ CSRF disabled (stateless API)</li>
 *   <li>无状态会话管理（JWT）/ Stateless session management (JWT)</li>
 *   <li>公开端点：/api/v1/auth/**, /swagger-ui/**, /actuator/health 等 / Public endpoints</li>
 *   <li>其他所有端点需要认证 / All other endpoints require authentication</li>
 *   <li>自定义 JWT 认证过滤器 / Custom JWT authentication filter</li>
 *   <li>BCrypt 密码编码器 / BCrypt password encoder</li>
 * </ul>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * 配置安全过滤器链
     * <p>
     * Configure security filter chain
     * </p>
     * <p>
     * 定义 HTTP 安全规则，包括 CSRF、会话管理、授权规则和异常处理。
     * </p>
     * <p>
     * Defines HTTP security rules, including CSRF, session management, authorization rules, and exception handling.
     * </p>
     *
     * @param http HTTP 安全配置构建器 / HTTP security configuration builder
     * @return 安全过滤器链 / Security filter chain
     * @throws Exception 如果配置失败 / if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（无状态 API 不需要）/ Disable CSRF (not needed for stateless API)
                .csrf(AbstractHttpConfigurer::disable)

                // 无状态会话管理（JWT）/ Stateless session management (JWT)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 配置授权规则 / Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // 公开端点（无需认证）/ Public endpoints (no authentication required)
                        .requestMatchers(
                                "/api/v1/auth/**",           // 注册和登录 / Register and login
                                "/swagger-ui/**", "/swagger-ui.html",  // Swagger UI
                                "/v3/api-docs/**", "/api-docs/**",      // OpenAPI 文档 / OpenAPI docs
                                "/actuator/health"          // 健康检查 / Health check
                        ).permitAll()
                        // 其他所有请求需要认证 / All other requests require authentication
                        .anyRequest().authenticated()
                )

                // 未认证时返回 JSON 401 而不是重定向 / Return 401 JSON instead of redirect when unauthenticated
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().write(
                            "{\"success\":false,\"error\":\"UNAUTHORIZED\","
                                    + "\"message\":\"Authentication required\","
                                    + "\"timestamp\":\"" + LocalDateTime.now() + "\"}"
                    );
                }))

                // 配置认证提供者 / Configure authentication provider
                .authenticationProvider(authenticationProvider())

                // 在 UsernamePasswordAuthenticationFilter 之前添加 JWT 过滤器
                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 防止 Spring Boot 自动注册 JwtAuthenticationFilter
     * <p>
     * Prevent Spring Boot from auto-registering JwtAuthenticationFilter
     * </p>
     * <p>
     * JWT 过滤器已经在安全过滤器链中注册，禁用作为原始 servlet 过滤器的自动注册。
     * </p>
     * <p>
     * JWT filter is already registered inside the Security filter chain, disable auto-registration as a raw servlet filter.
     * </p>
     *
     * @param filter JWT 认证过滤器 / JWT authentication filter
     * @return 过滤器注册 Bean / Filter registration bean
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
            JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> bean = new FilterRegistrationBean<>(filter);
        bean.setEnabled(false);
        return bean;
    }

    /**
     * 配置认证提供者
     * <p>
     * Configure authentication provider
     * </p>
     * <p>
     * 使用 DaoAuthenticationProvider 进行认证，加载用户详情和验证密码。
     * </p>
     * <p>
     * Uses DaoAuthenticationProvider for authentication, loads user details and validates password.
     * </p>
     *
     * @return 认证提供者 / Authentication provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * 配置密码编码器
     * <p>
     * Configure password encoder
     * </p>
     * <p>
     * 使用 BCrypt 算法对密码进行编码和验证。
     * </p>
     * <p>
     * Uses BCrypt algorithm for password encoding and verification.
     * </p>
     *
     * @return 密码编码器 / Password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置认证管理器
     * <p>
     * Configure authentication manager
     * </p>
     * <p>
     * 暴露为 Bean，以便 AuthServiceImpl 可以调用 authenticate() 进行登录。
     * </p>
     * <p>
     * Exposed as a bean so AuthServiceImpl can call authenticate() for login.
     * </p>
     *
     * @param config 认证配置 / Authentication configuration
     * @return 认证管理器 / Authentication manager
     * @throws Exception 如果配置失败 / if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
