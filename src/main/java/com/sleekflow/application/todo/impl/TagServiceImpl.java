package com.sleekflow.application.todo.impl;

import com.sleekflow.application.todo.ITagService;
import com.sleekflow.domain.todo.Tag;
import com.sleekflow.domain.user.User;
import com.sleekflow.infrastructure.security.UserContext;
import com.sleekflow.infrastructure.exception.DuplicateResourceException;
import com.sleekflow.infrastructure.exception.ForbiddenException;
import com.sleekflow.infrastructure.exception.ResourceNotFoundException;
import com.sleekflow.infrastructure.persistence.todo.TagRepository;
import com.sleekflow.infrastructure.persistence.user.UserRepository;
import com.sleekflow.interfaces.assembler.TodoAssembler;
import com.sleekflow.interfaces.dto.request.CreateTagRequest;
import com.sleekflow.interfaces.dto.response.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 标签服务实现
 * <p>
 * Tag Service Implementation
 * </p>
 * <p>
 * 实现标签（Tag）管理功能，包括创建、查询和删除用户私有标签。
 * </p>
 * <p>
 * Implements tag management functionality, including creation, querying, and deletion of user-private tags.
 * </p>
 * <p>
 * <b>标签私有性（Tag Privacy）：</b></p>
 * <p>
 * 每个标签都属于特定用户，不同用户可以拥有相同名称的标签。
 * 例如：Alice 和 Bob 都可以创建名为 "Work" 的标签，但它们互不干扰。
 * </p>
 * <p>
 * Each tag belongs to a specific user. Different users can have tags with the same name.
 * For example: Both Alice and Bob can create a tag named "Work", but they are independent.
 * </p>
 * <p>
 * <b>去重逻辑（Deduplication Logic）：</b></p>
 * <p>
 * 创建标签时，如果用户已存在同名标签，则返回现有标签（不创建重复标签）。
 * </p>
 * <p>
 * When creating a tag, if a tag with the same name already exists for the user,
 * returns the existing tag (no duplicates created).
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 * @see ITagService
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TagServiceImpl implements ITagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final TodoAssembler todoAssembler;

    @Override
    public TagResponse createTag(CreateTagRequest request) {
        String callerId = UserContext.getCurrentUserId();
        if (tagRepository.existsByNameAndUserId(request.getName(), callerId)) {
            throw new DuplicateResourceException("Tag already exists: " + request.getName());
        }

        User user = userRepository.findById(callerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", callerId));

        Tag tag = Tag.builder()
                .name(request.getName())
                .user(user)
                .build();

        tag = tagRepository.save(tag);
        return todoAssembler.toTagResponse(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagResponse> listTags() {
        String callerId = UserContext.getCurrentUserId();
        return tagRepository.findAllByUserId(callerId).stream()
                .map(todoAssembler::toTagResponse)
                .toList();
    }

    @Override
    public void deleteTag(String tagId) {
        String callerId = UserContext.getCurrentUserId();
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", tagId));

        if (!tag.getUser().getId().equals(callerId)) {
            throw new ForbiddenException("You do not own this tag");
        }

        tagRepository.delete(tag);
    }
}
