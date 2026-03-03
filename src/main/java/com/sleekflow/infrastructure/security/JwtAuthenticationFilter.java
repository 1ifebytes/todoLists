package com.sleekflow.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 * <p>
 * JWT Authentication Filter
 * </p>
 * <p>
 * 拦截每个请求，提取 Bearer 令牌，验证它，并设置 SecurityContext。
 * </p>
 * <p>
 * Intercepts every request, extracts the Bearer token, validates it, and sets the SecurityContext.
 * </p>
 * <p>
 * <b>注册说明（Registration Note）：</b></p>
 * <p>
 * 在 SecurityConfig 中注册（addFilterBefore），确保它在 UsernamePasswordAuthenticationFilter 之前运行。
 * </p>
 * <p>
 * Registration in SecurityConfig (addFilterBefore) ensures it runs before UsernamePasswordAuthenticationFilter.
 * </p>
 * <p>
 * SecurityConfig 中的 FilterRegistrationBean 禁用了作为原始 servlet 过滤器的自动注册。
 * </p>
 * <p>
 * FilterRegistrationBean in SecurityConfig disables auto-registration as a raw servlet filter.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * 执行过滤逻辑
     * <p>
     * Perform filter logic
     * </p>
     * <p>
     * 1. 从请求头中提取 JWT 令牌
     * </p>
     * <p>
     * 1. Extract JWT token from request header
     * </p>
     * <p>
     * 2. 验证令牌有效性
     * </p>
     * <p>
     * 2. Validate token validity
     * </p>
     * <p>
     * 3. 加载用户详情并设置 SecurityContext
     * </p>
     * <p>
     * 3. Load user details and set SecurityContext
     * </p>
     * <p>
     * 4. 继续过滤器链
     * </p>
     * <p>
     * 4. Continue filter chain
     * </p>
     *
     * @param request HTTP 请求 / HTTP request
     * @param response HTTP 响应 / HTTP response
     * @param filterChain 过滤器链 / Filter chain
     * @throws ServletException 如果发生 Servlet 异常 / if a servlet exception occurs
     * @throws IOException 如果发生 I/O 异常 / if an I/O exception occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 从请求中提取 JWT 令牌 / Extract JWT token from request
        String token = extractToken(request);

        try {
            // 如果令牌存在且有效，设置认证 / If token exists and is valid, set authentication
            if (token != null && jwtTokenProvider.isValid(token)) {
                String userId = jwtTokenProvider.getUserId(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 设置到 ThreadLocal，供 Service 层使用 / Set to ThreadLocal for Service layer use
                UserContext.setCurrentUserId(userId);
            }

            // 继续过滤器链 / Continue filter chain
            filterChain.doFilter(request, response);

        } finally {
            // 确保 ThreadLocal 被清理，防止内存泄漏 / Ensure ThreadLocal is cleared to prevent memory leak
            UserContext.clear();
        }
    }

    /**
     * 从请求头中提取 JWT 令牌
     * <p>
     * Extract JWT token from request header
     * </p>
     * <p>
     * 从 Authorization 请求头中提取 Bearer 令牌。
     * </p>
     * <p>
     * Extracts the Bearer token from the Authorization request header.
     * </p>
     * <p>
     * 格式：Authorization: Bearer &lt;token&gt;
     * </p>
     * <p>
     * Format: Authorization: Bearer &lt;token&gt;
     * </p>
     *
     * @param request HTTP 请求 / HTTP request
     * @return JWT 令牌字符串，如果不存在则返回 null / JWT token string, or null if not present
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);  // 跳过 "Bearer " 前缀 / Skip "Bearer " prefix
        }
        return null;
    }
}
