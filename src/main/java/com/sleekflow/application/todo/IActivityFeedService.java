package com.sleekflow.application.todo;

import com.sleekflow.domain.todo.Todo;
import com.sleekflow.domain.todo.enums.TodoAction;
import com.sleekflow.interfaces.dto.response.ActivityFeedResponse;
import com.sleekflow.interfaces.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

/**
 * 活动记录服务接口
 * <p>
 * Activity Feed Service Interface
 * </p>
 * <p>
 * 定义活动记录（Activity Feed）相关的业务操作，用于审计和跟踪待办事项的变更历史。
 * </p>
 * <p>
 * Defines business operations for activity feed, used for auditing and tracking todo item change history.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
public interface IActivityFeedService {

    /**
     * 记录活动日志
     * <p>
     * Record an activity entry
     * </p>
     * <p>
     * 在当前事务中记录一条活动日志。
     * </p>
     * <p>
     * Records an activity entry within the current transaction.
     * </p>
     * <p>
     * payload 参数是变更字段的 JSON 字符串快照，例如：
     * </p>
     * <p>
     * The payload parameter is a JSON string snapshot of the changed fields, for example:
     * </p>
     * <pre>
     * {"name":"Buy milk", "status":"COMPLETED"}
     * </pre>
     *
     * @param actorId 执行操作的用户 ID / ID of the user who performed the action
     * @param todo 关联的待办事项 / Associated todo item
     * @param action 操作类型 / Type of action performed
     * @param payload 变更字段的 JSON 快照 / JSON string snapshot of the changed fields
     */
    void record(String actorId, Todo todo, TodoAction action, String payload);

    /**
     * 查询待办事项的活动记录
     * <p>
     * List activity feed for a todo
     * </p>
     * <p>
     * 返回指定待办事项的所有活动记录，按时间倒序排列，支持分页。
     * 用户只能查询自己拥有的或已被分享的待办事项的活动记录。
     * </p>
     * <p>
     * Returns all activity records for the specified todo, sorted by time in descending order,
     * supporting pagination. Users can only query activity feeds for todos they own or have been shared with them.
     * </p>
     *
     * @param todoId 待办事项 ID / Todo item ID
     * @param pageable 分页和排序参数 / Pagination and sorting parameters
     * @return 分页活动记录列表响应 / Paginated activity feed list response
     */
    PageResponse<ActivityFeedResponse> listFeed(String todoId, Pageable pageable);
}
