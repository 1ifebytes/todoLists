package com.sleekflow.interfaces.dto.request;

import com.sleekflow.domain.todo.enums.TodoRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 分享待办事项请求类
 * <p>
 * Share Todo Request Class
 * </p>
 * <p>
 * 将待办事项分享给其他用户时提交的请求数据。
 * </p>
 * <p>
 * Request data submitted when sharing a todo item with another user.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShareTodoRequest {

    /**
     * 被分享用户的 ID
     * <p>
     * ID of the user to share with
     * </p>
     * <p>
     * 必填。
     * </p>
     * <p>
     * Required.
     * </p>
     */
    @NotBlank
    private String userId;

    /**
     * 授予的角色
     * <p>
     * Role to grant
     * </p>
     * <p>
     * 必填。
     * 服务层验证角色必须是 EDITOR 或 VIEWER。
     * </p>
     * <p>
     * Required.
     * Service layer validates that role is EDITOR or VIEWER.
     * </p>
     * <p>
     * OWNER 角色不能通过分享授予。
     * </p>
     * <p>
     * OWNER role cannot be granted via share.
     * </p>
     */
    // Service validates that role is EDITOR or VIEWER — OWNER cannot be granted via share.
    @NotNull
    private TodoRole role;
}
