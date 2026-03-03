package com.sleekflow.interfaces.dto.response;

import com.sleekflow.domain.team.enums.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 团队响应类
 * <p>
 * Team Response Class
 * </p>
 * <p>
 * 团队的详细信息。
 * </p>
 * <p>
 * Detailed information of a team.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponse {

    /**
     * 团队 ID
     * <p>
     * Team ID
     * </p>
     */
    private String id;

    /**
     * 团队名称
     * <p>
     * Team name
     * </p>
     */
    private String name;

    /**
     * 团队描述
     * <p>
     * Team description
     * </p>
     */
    private String description;

    /**
     * 团队创建者
     * <p>
     * Team creator
     * </p>
     * <p>
     * 可为空：如果创建者被删除，此字段为 null。
     * </p>
     * <p>
     * Nullable: null if the creator was deleted.
     * </p>
     */
    private UserSummaryResponse createdBy;

    /**
     * 团队成员数量
     * <p>
     * Team member count
     * </p>
     * <p>
     * 由服务层在映射后设置。
     * </p>
     * <p>
     * Set by service layer after mapping.
     * </p>
     */
    private int memberCount;

    /**
     * 调用者的有效角色
     * <p>
     * Caller's effective role
     * </p>
     * <p>
     * 调用者在此团队中的角色。
     * 由服务层在映射后设置。
     * </p>
     * <p>
     * Caller's role within this team.
     * Set by service layer after mapping.
     * </p>
     * <p>
     * 可能的值：ADMIN, MEMBER
     * </p>
     * <p>
     * Possible values: ADMIN, MEMBER
     * </p>
     */
    private TeamRole myRole;

    /**
     * 团队创建时间
     * <p>
     * Team creation timestamp
     * </p>
     */
    private LocalDateTime createdAt;

    /**
     * 团队最后更新时间
     * <p>
     * Team last update timestamp
     * </p>
     */
    private LocalDateTime updatedAt;
}
