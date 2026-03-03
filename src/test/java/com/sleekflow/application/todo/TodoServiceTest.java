package com.sleekflow.application.todo;

import com.sleekflow.application.todo.impl.TodoServiceImpl;
import com.sleekflow.domain.todo.Tag;
import com.sleekflow.domain.todo.Todo;
import com.sleekflow.domain.todo.TodoPermission;
import com.sleekflow.domain.todo.enums.Priority;
import com.sleekflow.domain.todo.enums.TodoAction;
import com.sleekflow.domain.todo.enums.TodoRole;
import com.sleekflow.domain.todo.enums.TodoStatus;
import com.sleekflow.domain.user.User;
import com.sleekflow.infrastructure.security.UserContext;
import com.sleekflow.infrastructure.exception.DuplicateResourceException;
import com.sleekflow.infrastructure.exception.ForbiddenException;
import com.sleekflow.infrastructure.exception.ResourceNotFoundException;
import com.sleekflow.infrastructure.persistence.todo.TagRepository;
import com.sleekflow.infrastructure.persistence.todo.TodoPermissionRepository;
import com.sleekflow.infrastructure.persistence.todo.TodoRepository;
import com.sleekflow.infrastructure.persistence.user.UserRepository;
import com.sleekflow.interfaces.assembler.TodoAssembler;
import com.sleekflow.interfaces.dto.request.CreateTodoRequest;
import com.sleekflow.interfaces.dto.request.ShareTodoRequest;
import com.sleekflow.interfaces.dto.request.UpdateTodoRequest;
import com.sleekflow.interfaces.dto.response.TodoPermissionResponse;
import com.sleekflow.interfaces.dto.response.TodoResponse;
import com.sleekflow.interfaces.dto.response.UserSummaryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock private TodoRepository todoRepository;
    @Mock private UserRepository userRepository;
    @Mock private TagRepository tagRepository;
    @Mock private TodoPermissionRepository todoPermissionRepository;
    @Mock private IActivityFeedService activityFeedService;
    @Mock private TodoAssembler todoAssembler;

    @InjectMocks
    private TodoServiceImpl todoService;

    private final User alice = User.builder().id("u1").email("alice@example.com").username("alice").build();
    private final User bob   = User.builder().id("u2").email("bob@example.com").username("bob").build();

    private Todo buildTodo(String id, User owner) {
        return Todo.builder().id(id).name("Buy milk").description("2% milk")
                .status(TodoStatus.NOT_STARTED).priority(Priority.MEDIUM)
                .owner(owner).tags(new HashSet<>()).permissions(new ArrayList<>()).build();
    }

    private TodoResponse buildTodoResponse(String id) {
        TodoResponse r = new TodoResponse();
        r.setId(id);
        r.setName("Buy milk");
        r.setOwner(new UserSummaryResponse("u1", "alice", "alice@example.com"));
        return r;
    }

    // ── createTodo ────────────────────────────────────────────────────────────

    @Test
    void createTodo_success_setsOwnerRoleAndRecordsActivity() {
        CreateTodoRequest req = new CreateTodoRequest("Buy milk", null, null, null, null, null);
        Todo saved = buildTodo("todo1", alice);
        TodoResponse mapped = buildTodoResponse("todo1");

        when(userRepository.findById("u1")).thenReturn(Optional.of(alice));
        when(todoRepository.save(any(Todo.class))).thenReturn(saved);
        when(todoAssembler.toResponse(saved)).thenReturn(mapped);

        UserContext.setCurrentUserId("u1");
        try {
            TodoResponse response = todoService.createTodo(req);

            assertThat(response.getMyRole()).isEqualTo(TodoRole.OWNER);
            verify(activityFeedService).record(eq("u1"), eq(saved), eq(TodoAction.TODO_CREATED), anyString());
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void createTodo_callerNotFound_throwsResourceNotFoundException() {
        when(userRepository.findById("u99")).thenReturn(Optional.empty());

        UserContext.setCurrentUserId("u99");
        try {
            assertThatThrownBy(() -> todoService.createTodo(new CreateTodoRequest("Buy milk", null, null, null, null, null)))
                    .isInstanceOf(ResourceNotFoundException.class);
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void createTodo_duplicateTagIds_deduplicatesAndSucceeds() {
        CreateTodoRequest req = new CreateTodoRequest("Buy milk", null, null, null, null, List.of("t1", "t1"));
        Tag tag = Tag.builder().id("t1").name("Work").user(alice).build();
        Todo saved = buildTodo("todo1", alice);
        TodoResponse mapped = buildTodoResponse("todo1");

        when(userRepository.findById("u1")).thenReturn(Optional.of(alice));
        when(tagRepository.findAllById(any())).thenReturn(List.of(tag));
        when(todoRepository.save(any(Todo.class))).thenReturn(saved);
        when(todoAssembler.toResponse(saved)).thenReturn(mapped);

        UserContext.setCurrentUserId("u1");
        try {
            TodoResponse response = todoService.createTodo(req);
            assertThat(response.getMyRole()).isEqualTo(TodoRole.OWNER);
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void createTodo_foreignTag_throwsForbiddenException() {
        CreateTodoRequest req = new CreateTodoRequest("Buy milk", null, null, null, null, List.of("t2"));
        Tag foreignTag = Tag.builder().id("t2").name("BobTag").user(bob).build();

        when(userRepository.findById("u1")).thenReturn(Optional.of(alice));
        when(tagRepository.findAllById(any())).thenReturn(List.of(foreignTag));

        UserContext.setCurrentUserId("u1");
        try {
            assertThatThrownBy(() -> todoService.createTodo(req))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessageContaining("do not belong to you");
        } finally {
            UserContext.clear();
        }
    }

    // ── getTodo ───────────────────────────────────────────────────────────────

    @Test
    void getTodo_asOwner_returnsOwnerRole() {
        Todo todo = buildTodo("todo1", alice);
        TodoResponse mapped = buildTodoResponse("todo1");

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(todoAssembler.toResponse(todo)).thenReturn(mapped);

        UserContext.setCurrentUserId("u1");
        try {
            TodoResponse response = todoService.getTodo("todo1");

            assertThat(response.getMyRole()).isEqualTo(TodoRole.OWNER);
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void getTodo_asSharedEditor_returnsEditorRole() {
        Todo todo = buildTodo("todo1", alice);
        TodoPermission perm = TodoPermission.builder().id("p1").todo(todo)
                .user(bob).role(TodoRole.EDITOR).build();
        TodoResponse mapped = buildTodoResponse("todo1");

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(todoPermissionRepository.findByTodoIdAndUserId("todo1", "u2")).thenReturn(Optional.of(perm));
        when(todoAssembler.toResponse(todo)).thenReturn(mapped);

        UserContext.setCurrentUserId("u2");
        try {
            TodoResponse response = todoService.getTodo("todo1");

            assertThat(response.getMyRole()).isEqualTo(TodoRole.EDITOR);
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void getTodo_noAccess_throwsForbiddenException() {
        Todo todo = buildTodo("todo1", alice);

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(todoPermissionRepository.findByTodoIdAndUserId("todo1", "u2")).thenReturn(Optional.empty());

        UserContext.setCurrentUserId("u2");
        try {
            assertThatThrownBy(() -> todoService.getTodo("todo1"))
                    .isInstanceOf(ForbiddenException.class);
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void getTodo_notFound_throwsResourceNotFoundException() {
        when(todoRepository.findById("todo99")).thenReturn(Optional.empty());

        UserContext.setCurrentUserId("u1");
        try {
            assertThatThrownBy(() -> todoService.getTodo("todo99"))
                    .isInstanceOf(ResourceNotFoundException.class);
        } finally {
            UserContext.clear();
        }
    }

    // ── updateTodo ────────────────────────────────────────────────────────────

    @Test
    void updateTodo_asOwner_updatesAndRecordsActivity() {
        Todo todo = buildTodo("todo1", alice);
        UpdateTodoRequest req = new UpdateTodoRequest("Buy oat milk", null, null, null, null, null);
        TodoResponse mapped = buildTodoResponse("todo1");

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);
        when(todoAssembler.toResponse(todo)).thenReturn(mapped);

        UserContext.setCurrentUserId("u1");
        try {
            todoService.updateTodo("todo1", req);

            assertThat(todo.getName()).isEqualTo("Buy oat milk");
            verify(activityFeedService).record(eq("u1"), eq(todo), eq(TodoAction.TODO_UPDATED), anyString());
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void updateTodo_asViewer_throwsForbiddenException() {
        Todo todo = buildTodo("todo1", alice);
        TodoPermission perm = TodoPermission.builder().id("p1").todo(todo)
                .user(bob).role(TodoRole.VIEWER).build();

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(todoPermissionRepository.findByTodoIdAndUserId("todo1", "u2")).thenReturn(Optional.of(perm));

        UserContext.setCurrentUserId("u2");
        try {
            assertThatThrownBy(() -> todoService.updateTodo("todo1",
                    new UpdateTodoRequest("x", null, null, null, null, null)))
                    .isInstanceOf(ForbiddenException.class);
        } finally {
            UserContext.clear();
        }
    }

    // ── deleteTodo ────────────────────────────────────────────────────────────

    @Test
    void deleteTodo_asOwner_softDeletesAndRecordsActivity() {
        Todo todo = buildTodo("todo1", alice);

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        UserContext.setCurrentUserId("u1");
        try {
            todoService.deleteTodo("todo1");

            assertThat(todo.getDeletedAt()).isNotNull();
            verify(activityFeedService).record(eq("u1"), eq(todo), eq(TodoAction.TODO_DELETED), anyString());
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void deleteTodo_asEditor_throwsForbiddenException() {
        Todo todo = buildTodo("todo1", alice);
        TodoPermission perm = TodoPermission.builder().id("p1").todo(todo)
                .user(bob).role(TodoRole.EDITOR).build();

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(todoPermissionRepository.findByTodoIdAndUserId("todo1", "u2")).thenReturn(Optional.of(perm));

        UserContext.setCurrentUserId("u2");
        try {
            assertThatThrownBy(() -> todoService.deleteTodo("todo1"))
                    .isInstanceOf(ForbiddenException.class);
        } finally {
            UserContext.clear();
        }
    }

    // ── shareTodo ─────────────────────────────────────────────────────────────

    @Test
    void shareTodo_success_savesPermissionAndRecordsActivity() {
        Todo todo = buildTodo("todo1", alice);
        ShareTodoRequest req = new ShareTodoRequest("u2", TodoRole.EDITOR);
        TodoPermission perm = TodoPermission.builder().id("p1").todo(todo)
                .user(bob).role(TodoRole.EDITOR).grantedBy(alice).build();
        TodoPermissionResponse permResponse = new TodoPermissionResponse("p1",
                new UserSummaryResponse("u2", "bob", "bob@example.com"),
                TodoRole.EDITOR, null, LocalDateTime.now());

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(todoPermissionRepository.existsByTodoIdAndUserId("todo1", "u2")).thenReturn(false);
        when(userRepository.findById("u2")).thenReturn(Optional.of(bob));
        when(userRepository.findById("u1")).thenReturn(Optional.of(alice));
        when(todoPermissionRepository.save(any(TodoPermission.class))).thenReturn(perm);
        when(todoAssembler.toPermissionResponse(perm)).thenReturn(permResponse);

        UserContext.setCurrentUserId("u1");
        try {
            TodoPermissionResponse result = todoService.shareTodo("todo1", req);

            assertThat(result.getRole()).isEqualTo(TodoRole.EDITOR);
            verify(activityFeedService).record(eq("u1"), eq(todo), eq(TodoAction.TODO_SHARED), anyString());
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void shareTodo_alreadyShared_throwsDuplicateResourceException() {
        Todo todo = buildTodo("todo1", alice);
        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(todoPermissionRepository.existsByTodoIdAndUserId("todo1", "u2")).thenReturn(true);

        UserContext.setCurrentUserId("u1");
        try {
            assertThatThrownBy(() -> todoService.shareTodo("todo1", new ShareTodoRequest("u2", TodoRole.EDITOR)))
                    .isInstanceOf(DuplicateResourceException.class);
        } finally {
            UserContext.clear();
        }
    }

    // ── unshareTodo ───────────────────────────────────────────────────────────

    @Test
    void unshareTodo_success_deletesPermissionAndRecordsActivity() {
        Todo todo = buildTodo("todo1", alice);

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(todoPermissionRepository.existsByTodoIdAndUserId("todo1", "u2")).thenReturn(true);

        UserContext.setCurrentUserId("u1");
        try {
            todoService.unshareTodo("todo1", "u2");

            verify(todoPermissionRepository).deleteByTodoIdAndUserId("todo1", "u2");
            verify(activityFeedService).record(eq("u1"), eq(todo), eq(TodoAction.TODO_UNSHARED), anyString());
        } finally {
            UserContext.clear();
        }
    }

    // ── listPermissions ───────────────────────────────────────────────────────

    @Test
    void listPermissions_asOwner_returnsPermissions() {
        Todo todo = buildTodo("todo1", alice);
        TodoPermission perm = TodoPermission.builder().id("p1").todo(todo)
                .user(bob).role(TodoRole.VIEWER).build();
        TodoPermissionResponse permResponse = new TodoPermissionResponse("p1",
                new UserSummaryResponse("u2", "bob", "bob@example.com"),
                TodoRole.VIEWER, null, LocalDateTime.now());

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(todoPermissionRepository.findAllByTodoId("todo1")).thenReturn(List.of(perm));
        when(todoAssembler.toPermissionResponseList(List.of(perm))).thenReturn(List.of(permResponse));

        UserContext.setCurrentUserId("u1");
        try {
            List<TodoPermissionResponse> result = todoService.listPermissions("todo1");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getRole()).isEqualTo(TodoRole.VIEWER);
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void listPermissions_asEditor_throwsForbiddenException() {
        Todo todo = buildTodo("todo1", alice);
        TodoPermission perm = TodoPermission.builder().id("p1").todo(todo)
                .user(bob).role(TodoRole.EDITOR).build();

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(todoPermissionRepository.findByTodoIdAndUserId("todo1", "u2")).thenReturn(Optional.of(perm));

        UserContext.setCurrentUserId("u2");
        try {
            assertThatThrownBy(() -> todoService.listPermissions("todo1"))
                    .isInstanceOf(ForbiddenException.class);
        } finally {
            UserContext.clear();
        }
    }
}
