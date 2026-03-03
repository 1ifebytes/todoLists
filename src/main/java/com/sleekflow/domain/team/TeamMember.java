package com.sleekflow.domain.team;

import com.sleekflow.domain.team.enums.TeamRole;
import com.sleekflow.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 团队成员实体
 * <p>
 * Team Member Entity
 * </p>
 * <p>
 * 表示团队成员及其在团队中的角色。
 * </p>
 * <p>
 * Represents a team member and their role within the team.
 * </p>
 * <p>
 * <b>业务规则（Business Rule）：</b></p>
 * <p>
 * 团队的最后一个 ADMIN 不能被移除（在 TeamServiceImpl 中强制执行）。
 * </p>
 * <p>
 * The last ADMIN of a team cannot be removed (enforced in TeamServiceImpl).
 * </p>
 * <p>
 * 这确保团队始终至少有一个管理员。
 * </p>
 * <p>
 * This ensures the team always has at least one administrator.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Entity
@Table(name = "team_members")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMember {

    /**
     * 团队成员记录唯一标识符
     * <p>
     * Team member record unique identifier
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 关联的团队
     * <p>
     * Associated team
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    /**
     * 团队成员用户
     * <p>
     * Team member user
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 成员在团队中的角色
     * <p>
     * Member's role within the team
     * </p>
     * <p>
     * ADMIN：拥有全部团队管理权限 / ADMIN: Full team management permissions
     * MEMBER：普通成员，只能查看 / MEMBER: Regular member, view-only
     * </p>
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TeamRole role;

    /**
     * 成员加入团队的时间
     * <p>
     * Timestamp when the member joined the team
     * </p>
     */
    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;
}
