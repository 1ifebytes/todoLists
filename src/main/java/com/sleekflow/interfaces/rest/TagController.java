package com.sleekflow.interfaces.rest;

import com.sleekflow.application.todo.ITagService;
import com.sleekflow.interfaces.dto.request.CreateTagRequest;
import com.sleekflow.interfaces.dto.response.ApiResponse;
import com.sleekflow.interfaces.dto.response.TagResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 标签控制器
 * <p>
 * Tag Controller
 * </p>
 * <p>
 * 提供标签（Tag）管理的 REST API 端点。
 * </p>
 * <p>
 * Provides REST API endpoints for Tag management.
 * </p>
 * <p>
 * <b>端点（Endpoints）：</b></p>
 * <ul>
 *   <li>GET /api/v1/tags - 查询用户的所有标签 / List all user's tags</li>
 *   <li>POST /api/v1/tags - 创建新标签 / Create a new tag</li>
 *   <li>DELETE /api/v1/tags/{id} - 删除标签 / Delete a tag</li>
 * </ul>
 * <p>
 * <b>权限说明（Permission Note）：</b>所有端点需要 JWT 认证。
 * </p>
 * <p>
 * All endpoints require JWT authentication.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tags", description = "Tag management")
public class TagController {

    private final ITagService tagService;

    /**
     * 查询用户的所有标签
     * <p>
     * List all tags owned by the authenticated user
     * </p>
     * <p>
     * 返回当前用户创建的所有标签，按创建时间倒序排列。
     * </p>
     * <p>
     * Returns all tags created by the current user, sorted by creation time in descending order.
     * </p>
     *
     * @return 包含标签列表的响应 / Response containing list of tags
     */
    @Operation(summary = "List all tags owned by the authenticated user")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TagResponse>>> listTags() {
        return ResponseEntity.ok(ApiResponse.success(tagService.listTags()));
    }

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
     * @param request 创建标签请求体 / Request body containing tag name
     * @return 包含创建的标签信息的响应，HTTP 201 状态码 / Response containing created tag with HTTP 201 status
     */
    @Operation(summary = "Create a new tag")
    @PostMapping
    public ResponseEntity<ApiResponse<TagResponse>> createTag(
            @Valid @RequestBody CreateTagRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(tagService.createTag(request)));
    }

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
     * @param id 标签 ID / Tag ID
     * @return HTTP 204 无内容响应 / HTTP 204 no content response
     */
    @Operation(summary = "Delete a tag")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(
            @PathVariable String id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
