package com.sleekflow.infrastructure.security;

import com.sleekflow.domain.user.User;
import com.sleekflow.infrastructure.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户详情服务实现
 * <p>
 * User Details Service Implementation
 * </p>
 * <p>
 * 通过 UUID（来自 JWT subject）加载用户 — 不是通过邮箱。
 * </p>
 * <p>
 * Loads user by UUID (from JWT subject) — not by email.
 * </p>
 * <p>
 * <b>查找方式说明（Lookup Method Note）：</b></p>
 * <p>
 * 基于邮箱的查找仅在登录时使用，在 AuthServiceImpl 内部。
 * </p>
     * <p>
     * Email-based lookup is used only at login time, inside AuthServiceImpl.
     * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 根据用户名加载用户详情
     * <p>
     * Load user details by username
     * </p>
     * <p>
     * 注意：此处的 "username" 参数实际上是用户 ID（UUID），
     * 因为这是从 JWT subject 调用的，而不是登录表单。
     * </p>
     * <p>
     * Note: The "username" parameter here is actually the user ID (UUID),
     * because this is called from JWT subject, not from a login form.
     * </p>
     * <p>
     * 登录时使用基于邮箱的查找，在 AuthServiceImpl 内部。
     * </p>
     * <p>
     * Email-based lookup is used at login time, inside AuthServiceImpl.
     * </p>
     *
     * @param userId 用户 ID（UUID）/ User ID (UUID)
     * @return 用户详情对象 / User details object
     * @throws UsernameNotFoundException 如果用户不存在 / if user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
        return UserPrincipal.create(user);
    }
}
