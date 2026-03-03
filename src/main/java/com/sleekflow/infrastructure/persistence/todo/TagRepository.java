package com.sleekflow.infrastructure.persistence.todo;

import com.sleekflow.domain.todo.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 标签仓储接口
 * <p>
 * Tag Repository Interface
 * </p>
 * <p>
 * 提供 Tag 实体的数据库访问操作。
 * </p>
 * <p>
 * Provides database access operations for the Tag entity.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, String> {

    /**
     * 查找用户的所有标签
     * <p>
     * Find all tags for a user
     * </p>
     *
     * @param userId 用户 ID / User ID
     * @return 用户的标签列表 / List of user's tags
     */
    List<Tag> findAllByUserId(String userId);

    /**
     * 根据名称和用户查找标签
     * <p>
     * Find tag by name and user
     * </p>
     * <p>
     * 用于检查用户是否已存在同名标签。
     * </p>
     * <p>
     * Used to check if a user already has a tag with the same name.
     * </p>
     *
     * @param name 标签名称 / Tag name
     * @param userId 用户 ID / User ID
     * @return 标签对象的 Optional 包装 / Optional wrapping the tag object
     */
    Optional<Tag> findByNameAndUserId(String name, String userId);

    /**
     * 检查用户是否已存在同名标签
     * <p>
     * Check if tag with same name exists for user
     * </p>
     *
     * @param name 标签名称 / Tag name
     * @param userId 用户 ID / User ID
     * @return true 如果标签已存在，否则 false / true if tag exists, false otherwise
     */
    boolean existsByNameAndUserId(String name, String userId);
}
