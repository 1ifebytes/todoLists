package com.sleekflow.application.todo.impl;

import com.sleekflow.application.todo.IActivityFeedService;
import com.sleekflow.domain.todo.ActivityFeed;
import com.sleekflow.domain.todo.Todo;
import com.sleekflow.domain.todo.enums.TodoAction;
import com.sleekflow.domain.user.User;
import com.sleekflow.infrastructure.security.UserContext;
import com.sleekflow.infrastructure.exception.ForbiddenException;
import com.sleekflow.infrastructure.exception.ResourceNotFoundException;
import com.sleekflow.infrastructure.persistence.todo.ActivityFeedRepository;
import com.sleekflow.infrastructure.persistence.todo.TodoPermissionRepository;
import com.sleekflow.infrastructure.persistence.todo.TodoRepository;
import com.sleekflow.infrastructure.persistence.user.UserRepository;
import com.sleekflow.interfaces.assembler.TodoAssembler;
import com.sleekflow.interfaces.dto.response.ActivityFeedResponse;
import com.sleekflow.interfaces.dto.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 活动记录服务实现
 * <p>
 * Activity Feed Service Implementation
 * </p>
 * <p>
 * 实现活动记录（Activity Feed）功能，用于审计和跟踪待办事项的变更历史。
 * </p>
 * <p>
 * Implements activity feed functionality for auditing and tracking todo item change history.
 * </p>
 * <p>
 * <b>记录内容（Recorded Information）：</b></p>
 * <ul>
 *   <li>执行操作的用户（Actor）</li>
 *   <li>操作类型（Action）：TODO_CREATED, TODO_UPDATED, TODO_DELETED, TODO_SHARED, TODO_UNSHARED</li>
 *   <li>变更字段的 JSON 快照（Payload）：记录具体变更内容</li>
 *   <li>时间戳（Timestamp）</li>
 * </ul>
 * <p>
 * <b>权限控制（Access Control）：</b></p>
 * <p>
 * 查询活动记录需要用户是待办事项的所有者或被分享的用户。
 * </p>
 * <p>
 * Querying activity feed requires the user to be the todo owner or have been granted access.
 * </p>
 * <p>
 * <b>record 方法说明（record Method Note）：</b></p>
 * <p>
 * {@link #record(String, Todo, TodoAction, String)} 方法接受显式的 {@code actorId} 参数，
 * 用于记录其他用户执行的操作（如系统自动操作）。当前用户通过 {@link UserContext} 获取。
 * </p>
 * <p>
 * The {@link #record(String, Todo, TodoAction, String)} method accepts an explicit {@code actorId}
 * parameter for recording actions performed by other users (e.g., system automated operations).
 * The current user is obtained via {@link UserContext}.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 * @see IActivityFeedService
 * @see com.sleekflow.domain.todo.enums.TodoAction
 */
@Service
@RequiredArgsConstructor
public class ActivityFeedServiceImpl implements IActivityFeedService {

    private final ActivityFeedRepository activityFeedRepository;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final TodoPermissionRepository todoPermissionRepository;
    private final TodoAssembler todoAssembler;

    @Override
    @Transactional
    public void record(String actorId, Todo todo, TodoAction action, String payload) {
        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", actorId));

        ActivityFeed feed = ActivityFeed.builder()
                .todo(todo)
                .actor(actor)
                .action(action)
                .payload(payload)
                .build();

        activityFeedRepository.save(feed);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActivityFeedResponse> listFeed(String todoId, Pageable pageable) {
        String callerId = UserContext.getCurrentUserId();
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new ResourceNotFoundException("Todo", "id", todoId));

        boolean isOwner = todo.getOwner().getId().equals(callerId);
        boolean hasPermission = todoPermissionRepository.existsByTodoIdAndUserId(todoId, callerId);

        if (!isOwner && !hasPermission) {
            throw new ForbiddenException("Access denied to activity feed");
        }

        Page<ActivityFeedResponse> page = activityFeedRepository
                .findAllByTodoId(todoId, pageable)
                .map(todoAssembler::toActivityResponse);

        return PageResponse.of(page);
    }
}
