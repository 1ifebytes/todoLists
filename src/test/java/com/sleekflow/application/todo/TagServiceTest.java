package com.sleekflow.application.todo;

import com.sleekflow.application.todo.impl.TagServiceImpl;
import com.sleekflow.domain.todo.Tag;
import com.sleekflow.domain.user.User;
import com.sleekflow.infrastructure.exception.DuplicateResourceException;
import com.sleekflow.infrastructure.exception.ForbiddenException;
import com.sleekflow.infrastructure.exception.ResourceNotFoundException;
import com.sleekflow.infrastructure.persistence.todo.TagRepository;
import com.sleekflow.infrastructure.persistence.user.UserRepository;
import com.sleekflow.infrastructure.security.UserContext;
import com.sleekflow.interfaces.assembler.TodoAssembler;
import com.sleekflow.interfaces.dto.request.CreateTagRequest;
import com.sleekflow.interfaces.dto.response.TagResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock private TagRepository tagRepository;
    @Mock private UserRepository userRepository;
    @Mock private TodoAssembler todoAssembler;

    @InjectMocks
    private TagServiceImpl tagService;

    private final User alice = User.builder().id("u1").email("alice@example.com").username("alice").build();

    // ── createTag ─────────────────────────────────────────────────────────────

    @Test
    void createTag_success_returnsTagResponse() {
        CreateTagRequest req = new CreateTagRequest("Work");
        Tag saved = Tag.builder().id("t1").name("Work").user(alice).build();

        when(tagRepository.existsByNameAndUserId("Work", "u1")).thenReturn(false);
        when(userRepository.findById("u1")).thenReturn(Optional.of(alice));
        when(tagRepository.save(any(Tag.class))).thenReturn(saved);
        when(todoAssembler.toTagResponse(saved)).thenReturn(new TagResponse("t1", "Work"));

        UserContext.setCurrentUserId("u1");
        try {
            TagResponse response = tagService.createTag(req);

            assertThat(response.getId()).isEqualTo("t1");
            assertThat(response.getName()).isEqualTo("Work");
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void createTag_duplicate_throwsDuplicateResourceException() {
        CreateTagRequest req = new CreateTagRequest("Work");
        when(tagRepository.existsByNameAndUserId("Work", "u1")).thenReturn(true);

        UserContext.setCurrentUserId("u1");
        try {
            assertThatThrownBy(() -> tagService.createTag(req))
                    .isInstanceOf(DuplicateResourceException.class);

            verify(tagRepository, never()).save(any());
        } finally {
            UserContext.clear();
        }
    }

    // ── listTags ──────────────────────────────────────────────────────────────

    @Test
    void listTags_returnsAllTagsForCaller() {
        Tag t1 = Tag.builder().id("t1").name("Work").user(alice).build();
        Tag t2 = Tag.builder().id("t2").name("Personal").user(alice).build();

        when(tagRepository.findAllByUserId("u1")).thenReturn(List.of(t1, t2));
        when(todoAssembler.toTagResponse(t1)).thenReturn(new TagResponse("t1", "Work"));
        when(todoAssembler.toTagResponse(t2)).thenReturn(new TagResponse("t2", "Personal"));

        UserContext.setCurrentUserId("u1");
        try {
            List<TagResponse> result = tagService.listTags();

            assertThat(result).hasSize(2);
        } finally {
            UserContext.clear();
        }
    }

    // ── deleteTag ─────────────────────────────────────────────────────────────

    @Test
    void deleteTag_success_deletesTag() {
        Tag tag = Tag.builder().id("t1").name("Work").user(alice).build();
        when(tagRepository.findById("t1")).thenReturn(Optional.of(tag));

        UserContext.setCurrentUserId("u1");
        try {
            tagService.deleteTag("t1");

            verify(tagRepository).delete(tag);
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void deleteTag_notOwner_throwsForbiddenException() {
        User bob = User.builder().id("u2").build();
        Tag tag = Tag.builder().id("t1").name("Work").user(bob).build();
        when(tagRepository.findById("t1")).thenReturn(Optional.of(tag));

        UserContext.setCurrentUserId("u1");
        try {
            assertThatThrownBy(() -> tagService.deleteTag("t1"))
                    .isInstanceOf(ForbiddenException.class);

            verify(tagRepository, never()).delete(any());
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void deleteTag_notFound_throwsResourceNotFoundException() {
        when(tagRepository.findById("t99")).thenReturn(Optional.empty());

        UserContext.setCurrentUserId("u1");
        try {
            assertThatThrownBy(() -> tagService.deleteTag("t99"))
                    .isInstanceOf(ResourceNotFoundException.class);
        } finally {
            UserContext.clear();
        }
    }
}
