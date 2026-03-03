package com.sleekflow.application.todo;

import com.sleekflow.application.todo.impl.ActivityFeedServiceImpl;
import com.sleekflow.domain.todo.ActivityFeed;
import com.sleekflow.domain.todo.Todo;
import com.sleekflow.domain.todo.enums.Priority;
import com.sleekflow.domain.todo.enums.TodoAction;
import com.sleekflow.domain.todo.enums.TodoStatus;
import com.sleekflow.domain.user.User;
import com.sleekflow.infrastructure.exception.ForbiddenException;
import com.sleekflow.infrastructure.exception.ResourceNotFoundException;
import com.sleekflow.infrastructure.persistence.todo.ActivityFeedRepository;
import com.sleekflow.infrastructure.persistence.todo.TodoPermissionRepository;
import com.sleekflow.infrastructure.persistence.todo.TodoRepository;
import com.sleekflow.infrastructure.persistence.user.UserRepository;
import com.sleekflow.infrastructure.security.UserContext;
import com.sleekflow.interfaces.assembler.TodoAssembler;
import com.sleekflow.interfaces.dto.response.ActivityFeedResponse;
import com.sleekflow.interfaces.dto.response.PageResponse;
import com.sleekflow.interfaces.dto.response.UserSummaryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityFeedServiceTest {

    @Mock private ActivityFeedRepository activityFeedRepository;
    @Mock private UserRepository userRepository;
    @Mock private TodoRepository todoRepository;
    @Mock private TodoPermissionRepository todoPermissionRepository;
    @Mock private TodoAssembler todoAssembler;

    @InjectMocks
    private ActivityFeedServiceImpl activityFeedService;

    private final User alice = User.builder().id("u1").email("alice@example.com").username("alice").build();
    private final Todo todo = Todo.builder().id("todo1").name("Buy milk")
            .status(TodoStatus.NOT_STARTED).priority(Priority.MEDIUM).owner(alice).build();

    // ── record ────────────────────────────────────────────────────────────────

    @Test
    void record_success_savesActivityFeed() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(alice));

        activityFeedService.record("u1", todo, TodoAction.TODO_CREATED, "{\"name\":\"Buy milk\"}");

        verify(activityFeedRepository).save(any(ActivityFeed.class));
    }

    @Test
    void record_actorNotFound_throwsResourceNotFoundException() {
        when(userRepository.findById("u99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityFeedService.record("u99", todo, TodoAction.TODO_CREATED, "{}"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── listFeed ──────────────────────────────────────────────────────────────

    @Test
    void listFeed_asOwner_returnsPageOfActivities() {
        Pageable pageable = PageRequest.of(0, 10);
        ActivityFeed feed = ActivityFeed.builder().id("a1").todo(todo).actor(alice)
                .action(TodoAction.TODO_CREATED).payload("{}").createdAt(LocalDateTime.now()).build();
        ActivityFeedResponse feedResponse = new ActivityFeedResponse("a1", "todo1",
                new UserSummaryResponse("u1", "alice", "alice@example.com"),
                TodoAction.TODO_CREATED, "{}", LocalDateTime.now());

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(activityFeedRepository.findAllByTodoId("todo1", pageable))
                .thenReturn(new PageImpl<>(List.of(feed)));
        when(todoAssembler.toActivityResponse(feed)).thenReturn(feedResponse);

        UserContext.setCurrentUserId("u1");
        try {
            PageResponse<ActivityFeedResponse> result = activityFeedService.listFeed("todo1", pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo("a1");
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void listFeed_asSharedUser_returnsPageOfActivities() {
        Pageable pageable = PageRequest.of(0, 10);
        User bob = User.builder().id("u2").build();
        ActivityFeed feed = ActivityFeed.builder().id("a1").todo(todo).actor(alice)
                .action(TodoAction.TODO_UPDATED).payload("{}").createdAt(LocalDateTime.now()).build();
        ActivityFeedResponse feedResponse = new ActivityFeedResponse("a1", "todo1",
                new UserSummaryResponse("u1", "alice", "alice@example.com"),
                TodoAction.TODO_UPDATED, "{}", LocalDateTime.now());

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        // bob is not owner but has permission
        when(todoPermissionRepository.existsByTodoIdAndUserId("todo1", "u2")).thenReturn(true);
        when(activityFeedRepository.findAllByTodoId("todo1", pageable))
                .thenReturn(new PageImpl<>(List.of(feed)));
        when(todoAssembler.toActivityResponse(feed)).thenReturn(feedResponse);

        UserContext.setCurrentUserId("u2");
        try {
            PageResponse<ActivityFeedResponse> result = activityFeedService.listFeed("todo1", pageable);

            assertThat(result.getContent()).hasSize(1);
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void listFeed_noAccess_throwsForbiddenException() {
        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(todoPermissionRepository.existsByTodoIdAndUserId("todo1", "u99")).thenReturn(false);

        UserContext.setCurrentUserId("u99");
        try {
            assertThatThrownBy(() -> activityFeedService.listFeed("todo1", PageRequest.of(0, 10)))
                    .isInstanceOf(ForbiddenException.class);
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void listFeed_todoNotFound_throwsResourceNotFoundException() {
        when(todoRepository.findById("todo99")).thenReturn(Optional.empty());

        UserContext.setCurrentUserId("u1");
        try {
            assertThatThrownBy(() -> activityFeedService.listFeed("todo99", PageRequest.of(0, 10)))
                    .isInstanceOf(ResourceNotFoundException.class);
        } finally {
            UserContext.clear();
        }
    }
}
