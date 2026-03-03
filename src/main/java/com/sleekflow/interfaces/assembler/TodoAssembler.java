package com.sleekflow.interfaces.assembler;

import com.sleekflow.domain.todo.ActivityFeed;
import com.sleekflow.domain.todo.Tag;
import com.sleekflow.domain.todo.Todo;
import com.sleekflow.domain.todo.TodoPermission;
import com.sleekflow.interfaces.dto.response.ActivityFeedResponse;
import com.sleekflow.interfaces.dto.response.TagResponse;
import com.sleekflow.interfaces.dto.response.TodoPermissionResponse;
import com.sleekflow.interfaces.dto.response.TodoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

/**
 * 待办事项实体组装器
 * <p>
 * Todo Entity Assembler
 * </p>
 * <p>
 * 使用 MapStruct 将 Todo 相关实体转换为 DTO。
 * </p>
 * <p>
 * Uses MapStruct to convert Todo-related entities to DTOs.
 * </p>
 * <p>
 * <b>映射说明（Mapping Notes）：</b></p>
 * <ul>
 *   <li>myRole 由服务层（resolveRole）计算，在此组装器运行后通过 TodoResponse.setMyRole() 设置 / myRole is computed by service layer and set after assembler runs</li>
 *   <li>UserAssembler 通过 'uses' 重用，用于所有 User → UserSummaryResponse 映射 / UserAssembler reused via 'uses' for User mappings</li>
 *   <li>permissions / deletedAt 不在 TodoResponse 中 — 自动忽略 / permissions and deletedAt not in TodoResponse — auto-ignored</li>
 * </ul>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = {UserAssembler.class})
public interface TodoAssembler {

    /**
     * 将 Todo 实体转换为 TodoResponse DTO
     * <p>
     * Convert Todo entity to TodoResponse DTO
     * </p>
     * <p>
     * myRole 字段被忽略，由服务层在映射后设置。
     * </p>
     * <p>
     * myRole field is ignored, set by service layer after mapping.
     * </p>
     * <p>
     * tags: Set&lt;Tag&gt; → List&lt;TagResponse&gt; — MapStruct 使用下面的 toTagResponse() 方法。
     * </p>
     * <p>
     * tags: Set&lt;Tag&gt; → List&lt;TagResponse&gt; — MapStruct uses toTagResponse() method below.
     * </p>
     *
     * @param todo 待办事项实体 / Todo entity
     * @return 待办事项响应 / Todo response
     */
    @Mapping(target = "myRole", ignore = true)
    TodoResponse toResponse(Todo todo);

    /**
     * 将 Tag 实体转换为 TagResponse DTO
     * <p>
     * Convert Tag entity to TagResponse DTO
     * </p>
     *
     * @param tag 标签实体 / Tag entity
     * @return 标签响应 / Tag response
     */
    TagResponse toTagResponse(Tag tag);

    /**
     * 将 Tag 集合转换为 TagResponse 列表
     * <p>
     * Convert Tag set to TagResponse list
     * </p>
     *
     * @param tags 标签集合 / Set of tags
     * @return 标签响应列表 / List of tag responses
     */
    List<TagResponse> toTagResponseList(Set<Tag> tags);

    /**
     * 将 TodoPermission 实体转换为 TodoPermissionResponse DTO
     * <p>
     * Convert TodoPermission entity to TodoPermissionResponse DTO
     * </p>
     * <p>
     * grantedBy 可为空 — MapStruct 对嵌套的 UserSummaryResponse 返回 null。
     * </p>
     * <p>
     * grantedBy is nullable — MapStruct returns null for the nested UserSummaryResponse.
     * </p>
     *
     * @param permission 待办事项权限实体 / Todo permission entity
     * @return 待办事项权限响应 / Todo permission response
     */
    TodoPermissionResponse toPermissionResponse(TodoPermission permission);

    /**
     * 将 TodoPermission 列表转换为 TodoPermissionResponse 列表
     * <p>
     * Convert TodoPermission list to TodoPermissionResponse list
     * </p>
     *
     * @param permissions 待办事项权限列表 / List of todo permissions
     * @return 待办事项权限响应列表 / List of todo permission responses
     */
    List<TodoPermissionResponse> toPermissionResponseList(List<TodoPermission> permissions);

    /**
     * 将 ActivityFeed 实体转换为 ActivityFeedResponse DTO
     * <p>
     * Convert ActivityFeed entity to ActivityFeedResponse DTO
     * </p>
     * <p>
     * todoId 从 ActivityFeed.todo.id 扁平化得到。
     * </p>
     * <p>
     * todoId is flattened from ActivityFeed.todo.id.
     * </p>
     *
     * @param activityFeed 活动记录实体 / Activity feed entity
     * @return 活动记录响应 / Activity feed response
     */
    @Mapping(target = "todoId", source = "todo.id")
    ActivityFeedResponse toActivityResponse(ActivityFeed activityFeed);
}
