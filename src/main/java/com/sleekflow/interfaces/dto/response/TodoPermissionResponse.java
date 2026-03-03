package com.sleekflow.interfaces.dto.response;

import com.sleekflow.domain.todo.enums.TodoRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 待办事项权限响应类
 * <p>
 * Todo Permission Response Class
 * </p>
 * <p>
 * 待办事项权限授权的详细信息。
 * </p>
 * <p>
 * Detailed information of a todo permission grant.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoPermissionResponse {

    /**
     * 权限记录 ID
     * <p>
     * Permission record ID
     * </p>
     */
    private String id;

    /**
     * 被授予权限的用户
     * <p>
     * User who was granted permission
     * </p>
     */
    private UserSummaryResponse user;

    /**
     * 授予的角色
     * <p>
     * Granted role
     * </p>
     * <p>
     * 可能的值：EDITOR, VIEWER
     * </p>
     * <p>
     * Possible values: EDITOR, VIEWER
     * </p>
     */
    private TodoRole role;

    /**
     * 授权用户（授予权限的用户）
     * <p>
     * User who granted this permission
     * </p>
     * <p>
     * 可为空：如果授权用户被删除，此字段为 null。
     * </p>
     * <p>
     * Nullable: null if the granting user was deleted.
     * </p>
     */
    private UserSummaryResponse grantedBy;

    /**
     * 权限授予时间
     * <p>
     * Permission grant timestamp
     * </p>
     */
    private LocalDateTime createdAt;
}
