package com.sleekflow.infrastructure.persistence.todo;

import com.sleekflow.domain.todo.ActivityFeed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 活动记录仓储接口
 * <p>
 * Activity Feed Repository Interface
 * </p>
 * <p>
 * 提供 ActivityFeed 实体的数据库访问操作。
 * </p>
 * <p>
 * Provides database access operations for the ActivityFeed entity.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Repository
public interface ActivityFeedRepository extends JpaRepository<ActivityFeed, String> {

    /**
     * 分页查询待办事项的活动记录
     * <p>
     * Find activities for a todo with pagination
     * </p>
     * <p>
     * 返回指定待办事项的活动记录，按 Pageable 排序
     * （调用者传递 Sort.by("createdAt").descending()）。
     * </p>
     * <p>
     * Returns activities for a todo sorted by the Pageable
     * (caller passes Sort.by("createdAt").descending()).
     * </p>
     *
     * @param todoId 待办事项 ID / Todo ID
     * @param pageable 分页和排序参数 / Pagination and sorting parameters
     * @return 分页活动记录 / Paginated activity records
     */
    Page<ActivityFeed> findAllByTodoId(String todoId, Pageable pageable);
}
