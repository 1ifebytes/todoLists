package com.sleekflow.application.todo.impl;

import com.sleekflow.application.todo.IActivityFeedService;
import com.sleekflow.application.todo.ITodoService;
import com.sleekflow.domain.todo.Tag;
import com.sleekflow.infrastructure.security.UserContext;
import com.sleekflow.domain.todo.Todo;
import com.sleekflow.domain.todo.TodoPermission;
import com.sleekflow.domain.todo.enums.Priority;
import com.sleekflow.domain.todo.enums.TodoAction;
import com.sleekflow.domain.todo.enums.TodoRole;
import com.sleekflow.domain.todo.enums.TodoStatus;
import com.sleekflow.domain.user.User;
import com.sleekflow.infrastructure.exception.DuplicateResourceException;
import com.sleekflow.infrastructure.exception.ForbiddenException;
import com.sleekflow.infrastructure.exception.ResourceNotFoundException;
import com.sleekflow.infrastructure.persistence.todo.TagRepository;
import com.sleekflow.infrastructure.persistence.todo.TodoFilterSpec;
import com.sleekflow.infrastructure.persistence.todo.TodoPermissionRepository;
import com.sleekflow.infrastructure.persistence.todo.TodoRepository;
import com.sleekflow.infrastructure.persistence.user.UserRepository;
import com.sleekflow.interfaces.assembler.TodoAssembler;
import com.sleekflow.interfaces.dto.request.CreateTodoRequest;
import com.sleekflow.interfaces.dto.request.ShareTodoRequest;
import com.sleekflow.interfaces.dto.request.UpdateTodoRequest;
import com.sleekflow.interfaces.dto.response.PageResponse;
import com.sleekflow.interfaces.dto.response.TodoPermissionResponse;
import com.sleekflow.interfaces.dto.response.TodoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 待办事项服务实现
 * <p>
 * Todo Service Implementation
 * </p>
 * <p>
 * 实现待办事项（Todo）的核心业务逻辑，包括 CRUD 操作、权限管理（分享/撤销）和活动记录。
 * </p>
 * <p>
 * Implements core business logic for Todo items, including CRUD operations,
 * permission management (share/unshare), and activity tracking.
 * </p>
 * <p>
 * <b>权限模型（Permission Model）：</b></p>
 * <ul>
 *   <li>OWNER（所有者）：完整控制权，可编辑、删除、分享待办事项</li>
 *   <li>EDITOR（编辑者）：可编辑待办事项内容，但不能删除或分享</li>
 *   <li>VIEWER（查看者）：只读访问</li>
 * </ul>
 * <p>
 * <b>权限检查（Authorization Checks）：</b></p>
 * <p>
 * 每个方法首先通过 {@link UserContext#getCurrentUserId()} 获取当前认证用户，
 * 然后调用 {@link #resolveRole(String, Todo)} 验证用户是否有权限执行该操作。
 * </p>
 * <p>
 * Every method first obtains the current authenticated user via {@link UserContext#getCurrentUserId()},
 * then calls {@link #resolveRole(String, Todo)} to verify the user has permission to perform the operation.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 * @see ITodoService
 * @see UserContext
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TodoServiceImpl implements ITodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final TodoPermissionRepository todoPermissionRepository;
    private final IActivityFeedService activityFeedService;
    private final TodoAssembler todoAssembler;

    @Override
    public TodoResponse createTodo(CreateTodoRequest request) {
        String callerId = UserContext.getCurrentUserId();
        User owner = userRepository.findById(callerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", callerId));

        Set<Tag> tags = resolveTags(request.getTagIds());

        Todo todo = Todo.builder()
                .name(request.getName())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .status(request.getStatus() != null ? request.getStatus() : TodoStatus.NOT_STARTED)
                .priority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM)
                .owner(owner)
                .tags(tags)
                .permissions(new ArrayList<>())
                .build();

        todo = todoRepository.save(todo);
        activityFeedService.record(callerId, todo, TodoAction.TODO_CREATED,
                "{\"name\":\"" + todo.getName() + "\"}");

        TodoResponse response = todoAssembler.toResponse(todo);
        response.setMyRole(TodoRole.OWNER);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TodoResponse> listTodos(TodoStatus status, Priority priority,
            LocalDateTime dueDateFrom, LocalDateTime dueDateTo, List<String> tagNames,
            Boolean owned, Pageable pageable) {
        String callerId = UserContext.getCurrentUserId();

        Specification<Todo> spec = TodoFilterSpec.build(callerId, status, priority,
                dueDateFrom, dueDateTo, tagNames, owned);
        Page<Todo> page = todoRepository.findAll(spec, pageable);

        Page<TodoResponse> responsePage = page.map(todo -> {
            TodoResponse response = todoAssembler.toResponse(todo);
            response.setMyRole(resolveRole(callerId, todo));
            return response;
        });

        return PageResponse.of(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public TodoResponse getTodo(String todoId) {
        String callerId = UserContext.getCurrentUserId();
        Todo todo = findTodoOrThrow(todoId);
        TodoRole role = resolveRole(callerId, todo);

        TodoResponse response = todoAssembler.toResponse(todo);
        response.setMyRole(role);
        return response;
    }

    @Override
    public TodoResponse updateTodo(String todoId, UpdateTodoRequest request) {
        String callerId = UserContext.getCurrentUserId();
        Todo todo = findTodoOrThrow(todoId);
        TodoRole role = resolveRole(callerId, todo);

        if (role == TodoRole.VIEWER) {
            throw new ForbiddenException("Viewers cannot update todos");
        }

        if (request.getName() != null)        todo.setName(request.getName());
        if (request.getDescription() != null) todo.setDescription(request.getDescription());
        if (request.getDueDate() != null)     todo.setDueDate(request.getDueDate());
        if (request.getStatus() != null)      todo.setStatus(request.getStatus());
        if (request.getPriority() != null)    todo.setPriority(request.getPriority());
        if (request.getTagIds() != null)      todo.setTags(resolveTags(request.getTagIds()));

        todo = todoRepository.save(todo);
        activityFeedService.record(callerId, todo, TodoAction.TODO_UPDATED, "{}");

        TodoResponse response = todoAssembler.toResponse(todo);
        response.setMyRole(role);
        return response;
    }

    @Override
    public void deleteTodo(String todoId) {
        String callerId = UserContext.getCurrentUserId();
        Todo todo = findTodoOrThrow(todoId);
        TodoRole role = resolveRole(callerId, todo);

        if (role != TodoRole.OWNER) {
            throw new ForbiddenException("Only the owner can delete a todo");
        }

        // Record before soft-delete so the todo is still accessible within the transaction.
        activityFeedService.record(callerId, todo, TodoAction.TODO_DELETED, "{}");
        todo.softDelete();
        todoRepository.save(todo);
    }

    @Override
    public TodoPermissionResponse shareTodo(String todoId, ShareTodoRequest request) {
        String callerId = UserContext.getCurrentUserId();
        Todo todo = findTodoOrThrow(todoId);
        TodoRole role = resolveRole(callerId, todo);

        if (role != TodoRole.OWNER) {
            throw new ForbiddenException("Only the owner can share a todo");
        }
        if (request.getRole() == TodoRole.OWNER) {
            throw new ForbiddenException("Cannot grant OWNER role via share");
        }
        if (todoPermissionRepository.existsByTodoIdAndUserId(todoId, request.getUserId())) {
            throw new DuplicateResourceException("User already has access to this todo");
        }

        User grantee = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));
        User granter = userRepository.findById(callerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", callerId));

        TodoPermission permission = TodoPermission.builder()
                .todo(todo)
                .user(grantee)
                .role(request.getRole())
                .grantedBy(granter)
                .build();

        permission = todoPermissionRepository.save(permission);
        activityFeedService.record(callerId, todo, TodoAction.TODO_SHARED,
                "{\"userId\":\"" + request.getUserId() + "\"}");

        return todoAssembler.toPermissionResponse(permission);
    }

    @Override
    public void unshareTodo(String todoId, String targetUserId) {
        String callerId = UserContext.getCurrentUserId();
        Todo todo = findTodoOrThrow(todoId);
        TodoRole role = resolveRole(callerId, todo);

        if (role != TodoRole.OWNER) {
            throw new ForbiddenException("Only the owner can revoke access");
        }
        if (!todoPermissionRepository.existsByTodoIdAndUserId(todoId, targetUserId)) {
            throw new ResourceNotFoundException("Permission", "userId", targetUserId);
        }

        todoPermissionRepository.deleteByTodoIdAndUserId(todoId, targetUserId);
        activityFeedService.record(callerId, todo, TodoAction.TODO_UNSHARED,
                "{\"userId\":\"" + targetUserId + "\"}");
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoPermissionResponse> listPermissions(String todoId) {
        String callerId = UserContext.getCurrentUserId();
        Todo todo = findTodoOrThrow(todoId);
        TodoRole role = resolveRole(callerId, todo);

        if (role != TodoRole.OWNER) {
            throw new ForbiddenException("Only the owner can list permissions");
        }

        return todoAssembler.toPermissionResponseList(todoPermissionRepository.findAllByTodoId(todoId));
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Todo findTodoOrThrow(String todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new ResourceNotFoundException("Todo", "id", todoId));
    }

    // Returns OWNER if callerId is the todo owner, otherwise looks up their explicit permission.
    // Throws ForbiddenException if the caller has no access at all.
    private TodoRole resolveRole(String callerId, Todo todo) {
        if (todo.getOwner().getId().equals(callerId)) {
            return TodoRole.OWNER;
        }
        return todoPermissionRepository.findByTodoIdAndUserId(todo.getId(), callerId)
                .map(TodoPermission::getRole)
                .orElseThrow(() -> new ForbiddenException("Access denied"));
    }

    private Set<Tag> resolveTags(List<String> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return new HashSet<>();
        }
        String callerId = UserContext.getCurrentUserId();

        // Deduplicate tagIds first to avoid false positives
        Set<String> uniqueTagIds = new HashSet<>(tagIds);
        List<Tag> tags = tagRepository.findAllById(uniqueTagIds);

        // Validate that all tags belong to the current user (security check)
        Set<Tag> filteredTags = tags.stream()
                .filter(tag -> tag.getUser().getId().equals(callerId))
                .collect(Collectors.toSet());

        // Compare with deduplicated tagIds, not original tagIds
        if (filteredTags.size() != uniqueTagIds.size()) {
            throw new ForbiddenException("One or more tags do not belong to you");
        }
        return filteredTags;
    }
}
