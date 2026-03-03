package com.sleekflow.domain.todo;

import com.sleekflow.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 标签实体
 * <p>
 * Tag Entity
 * </p>
 * <p>
 * 表示用户自定义的标签，用于组织和分类待办事项。
 * 标签是用户私有的，不同用户可以拥有相同名称的标签。
 * </p>
 * <p>
 * Represents a user-defined tag for organizing and categorizing todo items.
 * Tags are user-private: different users can have tags with the same name.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Entity
@Table(name = "tags")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    /**
     * 标签唯一标识符
     * <p>
     * Tag unique identifier
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 标签名称
     * <p>
     * Tag name
     * </p>
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 标签所属用户
     * <p>
     * User who owns this tag.
     * 标签是用户私有的，不同用户可以拥有相同名称的标签。
     * Tags are user-private: different users can have tags with the same name.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 标签创建时间
     * <p>
     * Tag creation timestamp
     * </p>
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
