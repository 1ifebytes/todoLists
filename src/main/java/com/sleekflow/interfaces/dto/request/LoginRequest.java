package com.sleekflow.interfaces.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户登录请求类
 * <p>
 * User Login Request Class
 * </p>
 * <p>
 * 用户登录时提交的请求数据。
 * </p>
 * <p>
 * Request data submitted during user login.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * 用户邮箱
     * <p>
     * User email address
     * </p>
     * <p>
     * 用于查找用户账户。
     * </p>
     * <p>
     * Used to look up user account.
     * </p>
     */
    @Email
    @NotBlank
    private String email;

    /**
     * 密码
     * <p>
     * Password
     * </p>
     * <p>
     * 用于验证用户身份。
     * </p>
     * <p>
     * Used to verify user identity.
     * </p>
     */
    @NotBlank
    private String password;
}
