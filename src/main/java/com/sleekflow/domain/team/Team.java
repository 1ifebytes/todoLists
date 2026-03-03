package com.sleekflow.domain.team;

import com.sleekflow.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 团队实体
 * <p>
 * Team Entity
 * </p>
 * <p>
 * 表示系统中的团队，用于组织用户。
 * </p>
 * <p>
 * Represents a team in the system, used for organizing users.
 * </p>
 * <p>
 * <b>重要说明（Important Note）：</b></p>
 * <p>
 * 团队独立于待办事项。团队成员身份不会授予待办事项访问权限。
 * </p>
 * <p>
 * Teams are independent of todos. Team membership here does NOT grant todo access.
 * </p>
 * <p>
 * 待办事项分享通过 TodoPermission 单独处理。
 * </p>
 * <p>
 * Todo sharing is handled separately via TodoPermission.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Entity
@Table(name = "teams")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    /**
     * 团队唯一标识符
     * <p>
     * Team unique identifier
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 团队名称
     * <p>
     * Team name
     * </p>
     */
    @Column(nullable = false)
    private String name;

    /**
     * 团队描述
     * <p>
     * Team description
     * </p>
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 团队创建者
     * <p>
     * Team creator
     * </p>
     * <p>
     * 可为空的外键：如果创建者的账户被删除，设置为 SET NULL，这样团队不会丢失。
     * </p>
     * <p>
     * Nullable FK — SET NULL if the creator's account is deleted so the team is not lost.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    /**
     * 团队成员列表
     * <p>
     * List of team members
     * </p>
     * <p>
     * 注意：使用构建器时，Service 层总是显式传递 List。
     * </p>
     * <p>
     * Note: Service layer always passes an explicit List when using the builder.
     * </p>
     */
    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<TeamMember> members = new ArrayList<>();

    /**
     * 团队创建时间
     * <p>
     * Team creation timestamp
     * </p>
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 团队最后更新时间
     * <p>
     * Team last update timestamp
     * </p>
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
