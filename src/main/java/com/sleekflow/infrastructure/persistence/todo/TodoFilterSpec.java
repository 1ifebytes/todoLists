package com.sleekflow.infrastructure.persistence.todo;

import com.sleekflow.domain.todo.Tag;
import com.sleekflow.domain.todo.Todo;
import com.sleekflow.domain.todo.TodoPermission;
import com.sleekflow.domain.todo.enums.Priority;
import com.sleekflow.domain.todo.enums.TodoStatus;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 待办事项过滤规范
 * <p>
 * Todo Filter Specification
 * </p>
 * <p>
 * 使用 JPA Specification 构建动态查询条件，用于待办事项的多维度过滤。
 * </p>
 * <p>
 * Uses JPA Specification to build dynamic query criteria for multi-dimensional filtering of todos.
 * </p>
 * <p>
 * <b>设计说明（Design Note）：</b></p>
 * <ul>
 *   <li>无状态工具类，所有方法都是静态的 / Stateless utility class, all methods are static</li>
 *   <li>每个参数都是可空的，null 表示"该维度不过滤" / Every parameter is nullable, null means "no filter on this dimension"</li>
 *   <li>可见性谓词始终应用：调用者只能看到他们拥有的或有明确 TodoPermission 的待办事项 / Visibility predicate is always applied</li>
 * </ul>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
public final class TodoFilterSpec {

    /**
     * 私有构造函数，防止实例化
     * <p>
     * Private constructor to prevent instantiation
     * </p>
     */
    private TodoFilterSpec() {}

    /**
     * 构建待办事项过滤规范
     * <p>
     * Build todo filter specification
     * </p>
     * <p>
     * 根据提供的参数构建 JPA Specification，支持多维度动态过滤。
     * </p>
     * <p>
     * Builds a JPA Specification based on provided parameters, supporting multi-dimensional dynamic filtering.
     * </p>
     * <p>
     * <b>过滤条件（Filter Conditions）：</b></p>
     * <ul>
     *   <li><b>可见性（Visibility）：</b>始终应用，只返回调用者拥有或被分享的待办事项 / Always applied, returns only owned or shared todos</li>
     *   <li><b>状态（Status）：</b>可选，按待办事项状态过滤 / Optional, filter by todo status</li>
     *   <li><b>优先级（Priority）：</b>可选，按优先级过滤 / Optional, filter by priority</li>
     *   <li><b>截止日期范围（Due Date Range）：</b>可选，按日期范围过滤 / Optional, filter by date range</li>
     *   <li><b>标签名称（Tag Names）：</b>可选，按标签过滤（至少匹配一个）/ Optional, filter by tags (match at least one)</li>
     *   <li><b>所有权（Ownership）：</b>可选，true=仅拥有的，false=仅分享的 / Optional, true=owned only, false=shared only</li>
     * </ul>
     *
     * @param callerId 调用者用户 ID / Caller user ID
     * @param status 状态过滤条件（可选）/ Status filter (optional)
     * @param priority 优先级过滤条件（可选）/ Priority filter (optional)
     * @param dueDateFrom 截止日期范围开始（可选）/ Due date range start (optional)
     * @param dueDateTo 截止日期范围结束（可选）/ Due date range end (optional)
     * @param tagNames 标签名称列表（可选）/ List of tag names (optional)
     * @param owned 所有权过滤（可选）/ Ownership filter (optional)
     * @return JPA Specification / JPA Specification
     */
    public static Specification<Todo> build(
            String callerId,
            TodoStatus status,
            Priority priority,
            LocalDateTime dueDateFrom,
            LocalDateTime dueDateTo,
            List<String> tagNames,
            Boolean owned) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // ── 可见性谓词（Visibility Predicate）────────────────────────────────────

            // isOwner: todos.owner_id = callerId
            Predicate isOwner = cb.equal(root.get("owner").get("id"), callerId);

            // hasPermission: EXISTS (SELECT 1 FROM todo_permissions WHERE todo_id = todos.id AND user_id = callerId)
            Subquery<String> permSub = query.subquery(String.class);
            Root<TodoPermission> permRoot = permSub.from(TodoPermission.class);
            permSub.select(permRoot.get("id"))
                    .where(
                            cb.equal(permRoot.get("todo").get("id"), root.get("id")),
                            cb.equal(permRoot.get("user").get("id"), callerId)
                    );
            Predicate hasPermission = cb.exists(permSub);

            // 应用所有权过滤 / Apply ownership filter
            if (Boolean.TRUE.equals(owned)) {
                // 仅拥有的 / Owned only
                predicates.add(isOwner);
            } else if (Boolean.FALSE.equals(owned)) {
                // 仅分享的 / Shared only
                predicates.add(hasPermission);
            } else {
                // 默认：所有可见的待办事项（拥有的 + 分享的）/ Default: all visible todos (owned + shared)
                predicates.add(cb.or(isOwner, hasPermission));
            }

            // ── 可选过滤器（Optional Filters）───────────────────────────────────────

            // 状态过滤 / Status filter
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // 优先级过滤 / Priority filter
            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }

            // 截止日期范围过滤 / Due date range filter
            if (dueDateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dueDate"), dueDateFrom));
            }

            if (dueDateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), dueDateTo));
            }

            // 标签过滤 / Tag filter
            // 过滤至少有一个标签匹配提供名称的待办事项。
            // Filter todos that have at least one tag matching any of the provided names.
            // INNER JOIN 排除没有匹配标签的待办事项。
            // query.distinct(true) 防止待办事项匹配多个标签时出现重复行。
            // INNER JOIN excludes todos with no matching tag.
            // query.distinct(true) prevents duplicate rows when a todo matches multiple tags.
            if (tagNames != null && !tagNames.isEmpty()) {
                Join<Todo, Tag> tagsJoin = root.join("tags", JoinType.INNER);
                predicates.add(tagsJoin.get("name").in(tagNames));
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
