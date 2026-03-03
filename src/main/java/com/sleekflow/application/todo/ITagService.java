package com.sleekflow.application.todo;

import com.sleekflow.interfaces.dto.request.CreateTagRequest;
import com.sleekflow.interfaces.dto.response.TagResponse;

import java.util.List;

/**
 * 标签服务接口
 * <p>
 * Tag Service Interface
 * </p>
 * <p>
 * 定义标签（Tag）相关的业务操作，包括创建、查询和删除标签。
 * 标签是用户私有的，不同用户可以拥有相同名称的标签。
 * </p>
 * <p>
 * Defines business operations for tags, including creation, querying, and deletion.
 * Tags are user-private: different users can have tags with the same name.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
public interface ITagService {

    /**
     * 创建新标签
     * <p>
     * Create a new tag
     * </p>
     * <p>
     * 为当前用户创建新标签。
     * 如果用户已存在同名标签，则返回现有标签（不创建重复标签）。
     * </p>
     * <p>
     * Creates a new tag for the current user.
     * If a tag with the same name already exists for the user, returns the existing tag (no duplicates).
     * </p>
     *
     * @param request 创建标签请求 / Request containing tag name
     * @return 创建或现有的标签响应 / Created or existing tag response
     */
    TagResponse createTag(CreateTagRequest request);

    /**
     * 查询用户的所有标签
     * <p>
     * List all tags for the user
     * </p>
     * <p>
     * 返回当前用户创建的所有标签，按创建时间倒序排列。
     * </p>
     * <p>
     * Returns all tags created by the current user, sorted by creation time in descending order.
     * </p>
     *
     * @return 标签列表响应 / List of tags response
     */
    List<TagResponse> listTags();

    /**
     * 删除标签
     * <p>
     * Delete a tag
     * </p>
     * <p>
     * 删除指定标签。只有标签的创建者可以删除。
     * 标签被删除后，关联的待办事项-标签关系也会被级联删除。
     * </p>
     * <p>
     * Deletes the specified tag. Only the tag creator can delete it.
     * When a tag is deleted, associated todo-tag relationships are also removed via cascade.
     * </p>
     *
     * @param tagId 标签 ID / Tag ID
     */
    void deleteTag(String tagId);
}
