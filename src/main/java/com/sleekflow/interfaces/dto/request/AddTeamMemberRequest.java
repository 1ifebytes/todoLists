package com.sleekflow.interfaces.dto.request;

import com.sleekflow.domain.team.enums.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 添加团队成员请求类
 * <p>
 * Add Team Member Request Class
 * </p>
 * <p>
 * 将用户添加到团队时提交的请求数据。
 * </p>
 * <p>
 * Request data submitted when adding a user to a team.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddTeamMemberRequest {

    /**
     * 要添加的用户 ID
     * <p>
     * ID of the user to add
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
     * </p>
     * <p>
     * Required.
     * </p>
     * <p>
     * 可能的值：ADMIN, MEMBER
     * </p>
     * <p>
     * Possible values: ADMIN, MEMBER
     * </p>
     */
    @NotNull
    private TeamRole role;
}
