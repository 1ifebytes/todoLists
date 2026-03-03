package com.sleekflow.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekflow.application.todo.ITagService;
import com.sleekflow.infrastructure.exception.ForbiddenException;
import com.sleekflow.infrastructure.exception.GlobalExceptionHandler;
import com.sleekflow.infrastructure.security.UserContext;
import com.sleekflow.interfaces.dto.request.CreateTagRequest;
import com.sleekflow.interfaces.dto.response.TagResponse;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TagControllerTest {

    @Mock ITagService tagService;
    @InjectMocks TagController tagController;

    MockMvc mockMvc;
    final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        UserContext.setCurrentUserId("u1");
        mockMvc = MockMvcBuilders.standaloneSetup(tagController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    // ── GET /tags ─────────────────────────────────────────────────────────────

    @Test
    void listTags_authenticated_returns200WithList() throws Exception {
        when(tagService.listTags()).thenReturn(List.of(
                new TagResponse("t1", "Work"),
                new TagResponse("t2", "Personal")));

        mockMvc.perform(get("/api/v1/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("Work"));
    }

    // ── POST /tags ────────────────────────────────────────────────────────────

    @Test
    void createTag_validRequest_returns201() throws Exception {
        CreateTagRequest req = new CreateTagRequest("Work");
        when(tagService.createTag(any())).thenReturn(new TagResponse("t1", "Work"));

        mockMvc.perform(post("/api/v1/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value("t1"))
                .andExpect(jsonPath("$.data.name").value("Work"));
    }

    @Test
    void createTag_blankName_returns400() throws Exception {
        CreateTagRequest req = new CreateTagRequest("");

        mockMvc.perform(post("/api/v1/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ── DELETE /tags/{id} ─────────────────────────────────────────────────────

    @Test
    void deleteTag_ownTag_returns204() throws Exception {
        doNothing().when(tagService).deleteTag("t1");

        mockMvc.perform(delete("/api/v1/tags/t1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTag_notOwner_returns403() throws Exception {
        doThrow(new ForbiddenException("You do not own this tag"))
                .when(tagService).deleteTag("t1");

        mockMvc.perform(delete("/api/v1/tags/t1"))
                .andExpect(status().isForbidden());
    }
}
