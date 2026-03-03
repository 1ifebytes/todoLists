package com.sleekflow.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekflow.application.todo.IActivityFeedService;
import com.sleekflow.application.todo.ITodoService;
import com.sleekflow.domain.todo.enums.Priority;
import com.sleekflow.domain.todo.enums.TodoRole;
import com.sleekflow.domain.todo.enums.TodoStatus;
import com.sleekflow.infrastructure.exception.ForbiddenException;
import com.sleekflow.infrastructure.exception.GlobalExceptionHandler;
import com.sleekflow.infrastructure.exception.ResourceNotFoundException;
import com.sleekflow.infrastructure.security.UserContext;
import com.sleekflow.interfaces.dto.request.CreateTodoRequest;
import com.sleekflow.interfaces.dto.request.ShareTodoRequest;
import com.sleekflow.interfaces.dto.request.UpdateTodoRequest;
import com.sleekflow.interfaces.dto.response.ActivityFeedResponse;
import com.sleekflow.interfaces.dto.response.PageResponse;
import com.sleekflow.interfaces.dto.response.TodoPermissionResponse;
import com.sleekflow.interfaces.dto.response.TodoResponse;
import com.sleekflow.interfaces.dto.response.UserSummaryResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TodoControllerTest {

    @Mock ITodoService todoService;
    @Mock IActivityFeedService activityFeedService;
    @InjectMocks TodoController todoController;

    MockMvc mockMvc;
    final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        UserContext.setCurrentUserId("u1");
        mockMvc = MockMvcBuilders.standaloneSetup(todoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    private TodoResponse buildTodoResponse() {
        TodoResponse r = new TodoResponse();
        r.setId("todo1");
        r.setName("Buy milk");
        r.setStatus(TodoStatus.NOT_STARTED);
        r.setPriority(Priority.MEDIUM);
        r.setOwner(new UserSummaryResponse("u1", "alice", "alice@example.com"));
        r.setTags(Collections.emptyList());
        r.setMyRole(TodoRole.OWNER);
        return r;
    }

    // ── POST /todos ───────────────────────────────────────────────────────────

    @Test
    void createTodo_validRequest_returns201() throws Exception {
        CreateTodoRequest req = new CreateTodoRequest("Buy milk", null, null, null, null, null);
        when(todoService.createTodo(any())).thenReturn(buildTodoResponse());

        mockMvc.perform(post("/api/v1/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("todo1"))
                .andExpect(jsonPath("$.data.myRole").value("OWNER"));
    }

    @Test
    void createTodo_blankName_returns400() throws Exception {
        CreateTodoRequest req = new CreateTodoRequest("", null, null, null, null, null);

        mockMvc.perform(post("/api/v1/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ── GET /todos ────────────────────────────────────────────────────────────

    @Test
    void listTodos_noFilters_returns200WithPage() throws Exception {
        PageResponse<TodoResponse> page = new PageResponse<>(
                List.of(buildTodoResponse()), 0, 20, 1L, 1, true);
        when(todoService.listTodos(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    // ── GET /todos/{id} ───────────────────────────────────────────────────────

    @Test
    void getTodo_exists_returns200() throws Exception {
        when(todoService.getTodo("todo1")).thenReturn(buildTodoResponse());

        mockMvc.perform(get("/api/v1/todos/todo1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("todo1"));
    }

    @Test
    void getTodo_notFound_returns404() throws Exception {
        when(todoService.getTodo("todo99"))
                .thenThrow(new ResourceNotFoundException("Todo", "todo99"));

        mockMvc.perform(get("/api/v1/todos/todo99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTodo_noAccess_returns403() throws Exception {
        when(todoService.getTodo("todo1"))
                .thenThrow(new ForbiddenException("Access denied"));

        mockMvc.perform(get("/api/v1/todos/todo1"))
                .andExpect(status().isForbidden());
    }

    // ── PUT /todos/{id} ───────────────────────────────────────────────────────

    @Test
    void updateTodo_asOwner_returns200() throws Exception {
        UpdateTodoRequest req = new UpdateTodoRequest("Buy oat milk", null, null, null, null, null);
        TodoResponse updated = buildTodoResponse();
        updated.setName("Buy oat milk");
        when(todoService.updateTodo(eq("todo1"), any())).thenReturn(updated);

        mockMvc.perform(put("/api/v1/todos/todo1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Buy oat milk"));
    }

    @Test
    void updateTodo_asViewer_returns403() throws Exception {
        UpdateTodoRequest req = new UpdateTodoRequest("x", null, null, null, null, null);
        when(todoService.updateTodo(eq("todo1"), any()))
                .thenThrow(new ForbiddenException("Viewers cannot update todos"));

        mockMvc.perform(put("/api/v1/todos/todo1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    // ── DELETE /todos/{id} ────────────────────────────────────────────────────

    @Test
    void deleteTodo_asOwner_returns204() throws Exception {
        doNothing().when(todoService).deleteTodo("todo1");

        mockMvc.perform(delete("/api/v1/todos/todo1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTodo_asEditor_returns403() throws Exception {
        doThrow(new ForbiddenException("Only the owner can delete a todo"))
                .when(todoService).deleteTodo("todo1");

        mockMvc.perform(delete("/api/v1/todos/todo1"))
                .andExpect(status().isForbidden());
    }

    // ── POST /todos/{id}/share ────────────────────────────────────────────────

    @Test
    void shareTodo_asOwner_returns201() throws Exception {
        ShareTodoRequest req = new ShareTodoRequest("u2", TodoRole.EDITOR);
        TodoPermissionResponse permResponse = new TodoPermissionResponse(
                "p1", new UserSummaryResponse("u2", "bob", "bob@example.com"),
                TodoRole.EDITOR, null, LocalDateTime.now());
        when(todoService.shareTodo(eq("todo1"), any())).thenReturn(permResponse);

        mockMvc.perform(post("/api/v1/todos/todo1/share")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.role").value("EDITOR"));
    }

    // ── GET /todos/{id}/permissions ───────────────────────────────────────────

    @Test
    void listPermissions_asOwner_returns200() throws Exception {
        when(todoService.listPermissions("todo1")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/todos/todo1/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    // ── DELETE /todos/{id}/share/{userId} ─────────────────────────────────────

    @Test
    void unshareTodo_asOwner_returns204() throws Exception {
        doNothing().when(todoService).unshareTodo("todo1", "u2");

        mockMvc.perform(delete("/api/v1/todos/todo1/share/u2"))
                .andExpect(status().isNoContent());
    }

    // ── GET /todos/{id}/activities ────────────────────────────────────────────

    @Test
    void getActivities_accessible_returns200() throws Exception {
        PageResponse<ActivityFeedResponse> page = new PageResponse<>(
                Collections.emptyList(), 0, 20, 0L, 0, true);
        when(activityFeedService.listFeed(eq("todo1"), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/todos/todo1/activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }
}
