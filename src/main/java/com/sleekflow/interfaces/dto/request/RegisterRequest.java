package com.sleekflow.interfaces.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户注册请求类
 * <p>
 * User Registration Request Class
 * </p>
 * <p>
 * 用户注册时提交的请求数据。
 * </p>
 * <p>
 * Request data submitted during user registration.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /**
     * 用户邮箱
     * <p>
     * User email address
     * </p>
     * <p>
     * 必须是有效的邮箱格式，且在整个系统中唯一。
     * </p>
     * <p>
     * Must be a valid email format and unique across the system.
     * </p>
     */
    @Email
    @NotBlank
    private String email;

    /**
     * 用户名
     * <p>
     * Username
     * </p>
     * <p>
     * 长度必须在 3-50 个字符之间。
     * </p>
     * <p>
     * Length must be between 3-50 characters.
     * </p>
     */
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    /**
     * 密码
     * <p>
     * Password
     * </p>
     * <p>
     * 长度必须在 8-100 个字符之间。
     * 密码将使用 BCrypt 算法加密后存储。
     * </p>
     * <p>
     * Length must be between 8-100 characters.
     * Password will be encrypted using BCrypt algorithm before storage.
     * </p>
     */
    @NotBlank
    @Size(min = 8, max = 100)
    private String password;
}
