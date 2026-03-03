package com.sleekflow.infrastructure.persistence.todo;

import com.sleekflow.domain.todo.Tag;
import com.sleekflow.domain.todo.Todo;
import com.sleekflow.domain.todo.TodoPermission;
import com.sleekflow.domain.todo.enums.Priority;
import com.sleekflow.domain.todo.enums.TodoRole;
import com.sleekflow.domain.todo.enums.TodoStatus;
import com.sleekflow.domain.user.User;
import com.sleekflow.infrastructure.persistence.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers(disabledWithoutDocker = true)
@Transactional
class TodoRepositoryTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired TodoRepository todoRepository;
    @Autowired UserRepository userRepository;
    @Autowired TagRepository tagRepository;
    @Autowired TodoPermissionRepository todoPermissionRepository;

    private User alice;
    private User bob;

    @BeforeEach
    void setUp() {
        alice = userRepository.save(User.builder()
                .email("alice@test.com").username("alice").passwordHash("hash").build());
        bob = userRepository.save(User.builder()
                .email("bob@test.com").username("bob").passwordHash("hash").build());
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Todo saveTodo(User owner, String name, TodoStatus status, Priority priority, LocalDateTime dueDate) {
        return todoRepository.save(Todo.builder()
                .name(name)
                .owner(owner)
                .status(status)
                .priority(priority)
                .dueDate(dueDate)
                .tags(new HashSet<>())
                .permissions(new ArrayList<>())
                .build());
    }

    // ── filter by status ──────────────────────────────────────────────────────

    @Test
    void filterByStatus_returnsOnlyMatching() {
        saveTodo(alice, "NotStarted", TodoStatus.NOT_STARTED, Priority.MEDIUM, null);
        saveTodo(alice, "InProgress", TodoStatus.IN_PROGRESS, Priority.MEDIUM, null);
        saveTodo(alice, "Completed",  TodoStatus.COMPLETED,   Priority.MEDIUM, null);

        var spec = TodoFilterSpec.build(alice.getId(), TodoStatus.IN_PROGRESS, null, null, null, null, null);
        Page<Todo> result = todoRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("InProgress");
    }

    // ── filter by priority + dueDate range combined ────────────────────────────

    @Test
    void filterByPriorityAndDueDateRange_combined() {
        LocalDateTime base = LocalDateTime.of(2025, 6, 1, 0, 0);
        saveTodo(alice, "High-TooEarly", TodoStatus.NOT_STARTED, Priority.HIGH, base.minusDays(10));
        saveTodo(alice, "High-InRange",  TodoStatus.NOT_STARTED, Priority.HIGH, base.plusDays(5));
        saveTodo(alice, "Low-InRange",   TodoStatus.NOT_STARTED, Priority.LOW,  base.plusDays(5));
        saveTodo(alice, "High-TooLate",  TodoStatus.NOT_STARTED, Priority.HIGH, base.plusDays(30));

        var spec = TodoFilterSpec.build(
                alice.getId(), null, Priority.HIGH,
                base, base.plusDays(15),
                null, null);
        Page<Todo> result = todoRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("High-InRange");
    }

    // ── sort by dueDate asc ───────────────────────────────────────────────────

    @Test
    void sortByDueDateAsc_correctOrder() {
        LocalDateTime base = LocalDateTime.of(2025, 6, 1, 0, 0);
        saveTodo(alice, "Third",  TodoStatus.NOT_STARTED, Priority.MEDIUM, base.plusDays(2));
        saveTodo(alice, "First",  TodoStatus.NOT_STARTED, Priority.MEDIUM, base);
        saveTodo(alice, "Second", TodoStatus.NOT_STARTED, Priority.MEDIUM, base.plusDays(1));

        var spec = TodoFilterSpec.build(alice.getId(), null, null, null, null, null, null);
        Page<Todo> result = todoRepository.findAll(spec,
                PageRequest.of(0, 10, Sort.by("dueDate").ascending()));

        List<String> names = result.getContent().stream().map(Todo::getName).toList();
        assertThat(names).containsExactly("First", "Second", "Third");
    }

    // ── pagination ────────────────────────────────────────────────────────────

    @Test
    void pagination_page0_returnsFirstN_page1_returnsRest() {
        for (int i = 1; i <= 5; i++) {
            saveTodo(alice, "Todo-" + i, TodoStatus.NOT_STARTED, Priority.MEDIUM, null);
        }

        var spec = TodoFilterSpec.build(alice.getId(), null, null, null, null, null, null);
        Page<Todo> page0 = todoRepository.findAll(spec, PageRequest.of(0, 3));
        Page<Todo> page1 = todoRepository.findAll(spec, PageRequest.of(1, 3));

        assertThat(page0.getContent()).hasSize(3);
        assertThat(page0.getTotalElements()).isEqualTo(5);
        assertThat(page0.getTotalPages()).isEqualTo(2);
        assertThat(page1.getContent()).hasSize(2);
        assertThat(page1.isLast()).isTrue();
    }

    // ── soft delete excluded ──────────────────────────────────────────────────

    @Test
    void softDelete_excludedFromQueryResults() {
        saveTodo(alice, "Active", TodoStatus.NOT_STARTED, Priority.MEDIUM, null);
        Todo deleted = saveTodo(alice, "Deleted", TodoStatus.NOT_STARTED, Priority.MEDIUM, null);
        deleted.softDelete();
        todoRepository.saveAndFlush(deleted);

        var spec = TodoFilterSpec.build(alice.getId(), null, null, null, null, null, null);
        Page<Todo> result = todoRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Active");
    }

    // ── owned filter ──────────────────────────────────────────────────────────

    @Test
    void ownedFilter_true_returnsOnlyOwnedTodos() {
        Todo aliceTodo = saveTodo(alice, "Alice-Own", TodoStatus.NOT_STARTED, Priority.MEDIUM, null);
        Todo bobTodo   = saveTodo(bob,   "Bob-Shared", TodoStatus.NOT_STARTED, Priority.MEDIUM, null);

        // share bob's todo with alice
        todoPermissionRepository.save(TodoPermission.builder()
                .todo(bobTodo).user(alice).role(TodoRole.EDITOR).build());

        var spec = TodoFilterSpec.build(alice.getId(), null, null, null, null, null, Boolean.TRUE);
        Page<Todo> result = todoRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Alice-Own");
    }

    @Test
    void ownedFilter_false_returnsOnlySharedTodos() {
        saveTodo(alice, "Alice-Own", TodoStatus.NOT_STARTED, Priority.MEDIUM, null);
        Todo bobTodo = saveTodo(bob, "Bob-Shared", TodoStatus.NOT_STARTED, Priority.MEDIUM, null);

        todoPermissionRepository.save(TodoPermission.builder()
                .todo(bobTodo).user(alice).role(TodoRole.VIEWER).build());

        var spec = TodoFilterSpec.build(alice.getId(), null, null, null, null, null, Boolean.FALSE);
        Page<Todo> result = todoRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Bob-Shared");
    }

    // ── visibility (owned + shared, excluding private) ────────────────────────

    @Test
    void visibilityFilter_returnsOwnedAndShared_excludesPrivate() {
        saveTodo(alice, "Alice-Own",     TodoStatus.NOT_STARTED, Priority.MEDIUM, null);
        Todo bobShared  = saveTodo(bob, "Bob-Shared",   TodoStatus.NOT_STARTED, Priority.MEDIUM, null);
        saveTodo(bob,   "Bob-Private",   TodoStatus.NOT_STARTED, Priority.MEDIUM, null);

        todoPermissionRepository.save(TodoPermission.builder()
                .todo(bobShared).user(alice).role(TodoRole.VIEWER).build());

        var spec = TodoFilterSpec.build(alice.getId(), null, null, null, null, null, null);
        Page<Todo> result = todoRepository.findAll(spec, PageRequest.of(0, 10));

        List<String> names = result.getContent().stream().map(Todo::getName).toList();
        assertThat(names).containsExactlyInAnyOrder("Alice-Own", "Bob-Shared");
    }

    // ── tag filter ────────────────────────────────────────────────────────────

    @Test
    void filterByTagName_returnsOnlyTodosWithMatchingTag() {
        Tag work     = tagRepository.save(Tag.builder().name("Work").user(alice).build());
        Tag personal = tagRepository.save(Tag.builder().name("Personal").user(alice).build());

        Todo workTodo     = saveTodo(alice, "Work-Todo",     TodoStatus.NOT_STARTED, Priority.MEDIUM, null);
        Todo personalTodo = saveTodo(alice, "Personal-Todo", TodoStatus.NOT_STARTED, Priority.MEDIUM, null);
        saveTodo(alice, "NoTag-Todo", TodoStatus.NOT_STARTED, Priority.MEDIUM, null);

        workTodo.getTags().add(work);
        todoRepository.saveAndFlush(workTodo);
        personalTodo.getTags().add(personal);
        todoRepository.saveAndFlush(personalTodo);

        var spec = TodoFilterSpec.build(alice.getId(), null, null, null, null, List.of("Work"), null);
        Page<Todo> result = todoRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Work-Todo");
    }
}
